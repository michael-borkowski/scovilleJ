package at.borkowski.scovillej.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import at.borkowski.scovillej.impl.series.NumberSeriesImpl;
import at.borkowski.scovillej.profile.Series;
import at.borkowski.scovillej.profile.SeriesProvider;
import at.borkowski.scovillej.profile.SeriesResult;
import at.borkowski.scovillej.simulation.PhaseHandler;
import at.borkowski.scovillej.simulation.ServiceProvider;
import at.borkowski.scovillej.simulation.Simulation;
import at.borkowski.scovillej.simulation.SimulationContext;
import at.borkowski.scovillej.simulation.SimulationEvent;
import at.borkowski.scovillej.simulation.SimulationInitializationContext;
import at.borkowski.scovillej.simulation.SimulationMember;

/**
 * An implementation for {@link Simulation}.
 */
public class SimulationImpl implements Simulation {
   private final long totalTicks;
   private final List<String> phases;
   private final Collection<SimulationMember> members = new LinkedList<>();
   private final Map<String, List<PhaseHandler>> phaseToHandlers = new HashMap<>();
   private final Map<Long, List<SimulationEvent>> tickToEvents = new HashMap<>();
   private final Map<String, SeriesProvider<?>> series;
   private final Set<ServiceProvider<?>> services;

   private SimulationInitializationContext initializationContext;
   private SimulationContext simulationContext;

   private long currentTick = 0;
   private String currentPhase = null;
   private boolean done = false;

   /**
    * Creates a new simulation. The returned simulation is standing at tick 0
    * before processing.
    * 
    * @param totalTicks
    *           the total ticks this simulation must run for
    * @param phases
    *           a list of phases to be used
    * @param members
    *           a list of members to be serviced
    * @param series
    *           a list of series to be recorded
    * @param services
    *           a list of services to be provided
    */
   public SimulationImpl(long totalTicks, List<String> phases, List<SimulationMember> members, Map<String, SeriesProvider<?>> series, Set<ServiceProvider<?>> services) {
      this.totalTicks = totalTicks;
      this.phases = phases;
      this.series = series;
      this.services = services;

      this.members.addAll(members);
      this.members.addAll(services);

      for (String phase : phases)
         phaseToHandlers.put(phase, new LinkedList<>());

      initializeContexts();

      for (SeriesProvider<?> provider : series.values())
         provider.initialize(this);

      for (SimulationMember member : members)
         member.initialize(this, initializationContext);
      for (SimulationMember member : services)
         member.initialize(this, initializationContext);

      Collection<SimulationEvent> memberEvents;
      for (SimulationMember member : this.members) {
         if ((memberEvents = member.generateEvents()) != null) {
            for (SimulationEvent event : memberEvents) {
               List<SimulationEvent> list;
               if ((list = tickToEvents.get(event.getScheduledTick())) == null)
                  tickToEvents.put(event.getScheduledTick(), list = new LinkedList<SimulationEvent>());
               list.add(event);
            }
         }
      }

      for (SimulationMember member : this.members)
         if (member.getPhaseHandlers() != null)
            for (PhaseHandler handler : member.getPhaseHandlers())
               if (handler.getPhaseSubcription() != null)
                  for (String phase : handler.getPhaseSubcription())
                     phaseToHandlers.get(phase).add(handler);
               else
                  phaseToHandlers.get(Simulation.TICK_PHASE).add(handler);
   }

   private void initializeContexts() {
      initializationContext = new SimulationInitializationContext() {
         @SuppressWarnings("unchecked")
         @Override
         public <T> Series<T> getSeries(String symbol, Class<T> clazz) {
            if (series.get(symbol) == null)
               series.put(symbol, createSeries(clazz));
            if (!series.get(symbol).getValueClass().equals(clazz))
               return null;
            return (Series<T>) series.get(symbol);
         }

         @Override
         public <T> T getService(Class<T> clazz) {
            return lookup(clazz);
         }
      };
      simulationContext = new SimulationContext() {
         @Override
         public <T> T getService(Class<T> clazz) {
            return initializationContext.getService(clazz);
         }

         @Override
         public <T> Series<T> getSeries(String symbol, Class<T> clazz) {
            return initializationContext.getSeries(symbol, clazz);
         }
         @Override
         public String getCurrentPhase() {
            return currentPhase;
         }

         @Override
         public long getCurrentTick() {
            return currentTick;
         }
      };
   }

   @Override
   public void executeToEnd() {
      if (done)
         increaseTick();

      while (currentTick < totalTicks) {
         executeTick();
         currentTick++;
      }
      currentTick--;
   }

   @Override
   public void executeUpToTick(long tick) {
      if (done)
         increaseTick();
      if (tick >= totalTicks)
         throw new IllegalArgumentException("tick >= tickCount");
      if (tick <= currentTick)
         throw new IllegalArgumentException("tick <= tickCount");

      while (currentTick < tick) {
         executeTick();
         currentTick++;
      }
      currentTick--;
   }

   private void executeTick() {
      for (final String phase : phases) {
         currentPhase = phase;
         if (tickToEvents.containsKey(currentTick))
            for (SimulationEvent event : tickToEvents.get(currentTick))
               if ((event.getPhaseSubcription() != null && event.getPhaseSubcription().contains(currentPhase)) || (event.getPhaseSubcription() == null && currentPhase.equals(Simulation.TICK_PHASE)))
                  handlePhase(event);

         for (PhaseHandler handler : phaseToHandlers.get(currentPhase))
            handlePhase(handler);
      }

      done = true;
   }

   private void handlePhase(PhaseHandler handler) {
      handler.executePhase(simulationContext);
   }

   private SeriesProvider<?> createSeries(Class<?> clazz) {
      SeriesProvider<?> provider = NumberSeriesImpl.createIfKnown(clazz);
      if (provider != null)
         provider.initialize(this);
      return provider;
   }

   @SuppressWarnings("unchecked")
   private <T> T lookup(Class<T> clazz) {
      for (ServiceProvider<?> o : services)
         if (clazz.isAssignableFrom(o.getServiceClass()))
            return (T) o.getService();
      return null;
   }

   @Override
   public void executeCurrentTick() {
      if (done)
         throw new IllegalStateException("can't re-execute tick");
      executeTick();
   }

   @Override
   public long getCurrentTick() {
      return currentTick;
   }

   @Override
   public void executeAndIncreaseTick() {
      if (done)
         throw new IllegalStateException("can't re-execute tick");
      executeTick();
      if (currentTick + 1 < totalTicks) {
         currentTick++;
         done = false;
      }
   }

   @Override
   public void increaseTick() {
      increaseTick(false);
   }

   @Override
   public void increaseTickStrictly() {
      increaseTick(true);
   }

   private void increaseTick(boolean strict) {
      if (!done)
         throw new IllegalStateException("can't skip tick");

      if (currentTick + 1 < totalTicks) {
         currentTick++;
         done = false;
      } else if (strict)
         throw new IllegalStateException("can't advance past last tick");
   }

   @Override
   public boolean executedCurrentTick() {
      return done;
   }

   @Override
   public long getTotalTicks() {
      return totalTicks;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> SeriesResult<T> getSeries(String symbol, Class<T> clazz) {
      if (series.get(symbol) == null)
         return null;
      if (!series.get(symbol).getValueClass().equals(clazz))
         return null;
      return (SeriesResult<T>) series.get(symbol);
   }

   /**
    * Testability method only.
    * 
    * @return the internal events map
    */
   public Map<Long, List<SimulationEvent>> test__getEventsMap() {
      return tickToEvents;
   }

   @Override
   public List<String> getPhases() {
      return phases;
   }
}
