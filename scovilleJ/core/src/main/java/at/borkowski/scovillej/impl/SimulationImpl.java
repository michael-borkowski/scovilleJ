package at.borkowski.scovillej.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import at.borkowski.scovillej.profile.Series;
import at.borkowski.scovillej.profile.SeriesProvider;
import at.borkowski.scovillej.profile.SeriesResult;
import at.borkowski.scovillej.simulation.PhaseHandler;
import at.borkowski.scovillej.simulation.ServiceProvider;
import at.borkowski.scovillej.simulation.Simulation;
import at.borkowski.scovillej.simulation.SimulationContext;
import at.borkowski.scovillej.simulation.SimulationEvent;
import at.borkowski.scovillej.simulation.SimulationMember;

/**
 * An implementation for {@link Simulation}.
 */
// TODO check coverage
// TODO check whether processing for the last tick is correct
public class SimulationImpl implements Simulation {
   private final long totalTicks;
   private final List<String> phases;
   private final Collection<SimulationMember> members = new LinkedList<>();
   private final Map<String, List<PhaseHandler>> phaseToHandlers = new HashMap<>();
   private final Map<Long, List<SimulationEvent>> tickToEvents = new HashMap<>();
   private final Map<String, SeriesProvider<?>> series;
   private final Set<ServiceProvider<?>> services;

   private long currentTick = 0;
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

      Collection<SimulationEvent> memberEvents;
      for (SimulationMember member : members) {
         if ((memberEvents = member.generateEvents()) != null) {
            for (SimulationEvent event : memberEvents) {
               List<SimulationEvent> list;
               if ((list = tickToEvents.get(event.getScheduledTick())) == null)
                  tickToEvents.put(event.getScheduledTick(), list = new LinkedList<SimulationEvent>());
               list.add(event);
            }
         }
      }

      for (SeriesProvider<?> provider : series.values())
         provider.initialize(this);

      for (SimulationMember member : this.members)
         if (member.getPhaseHandlers() != null)
            for (PhaseHandler handler : member.getPhaseHandlers())
               if (handler.getPhaseSubcription() != null)
                  for (String phase : handler.getPhaseSubcription())
                     phaseToHandlers.get(phase).add(handler);
               else
                  phaseToHandlers.get(Simulation.TICK_PHASE).add(handler);
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
      for (final String currentPhase : phases) {
         if (tickToEvents.containsKey(currentTick))
            for (SimulationEvent event : tickToEvents.get(currentPhase))
               if (event.getPhaseSubcription().contains(currentPhase))
                  handlePhase(currentPhase, event);

         for (PhaseHandler handler : phaseToHandlers.get(currentPhase))
            handlePhase(currentPhase, handler);
      }

      done = true;
   }

   private void handlePhase(final String currentPhase, PhaseHandler handler) {
      handler.executePhase(new SimulationContext() {
         @Override
         public String getCurrentPhase() {
            return currentPhase;
         }

         @Override
         public long getCurrentTick() {
            return currentTick;
         }

         @SuppressWarnings("unchecked")
         @Override
         public <T extends Number> Series<T> getSeries(String symbol, Class<T> clazz) {
            if (series.get(symbol) == null)
               return null;
            if (!series.get(symbol).getValueClass().equals(clazz))
               return null;
            return (Series<T>) series.get(symbol);
         }

         @Override
         public <T> T getService(Class<T> clazz) {
            return lookup(clazz);
         }
      });
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
      currentTick++;
      done = false;
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
   public <T extends Number> SeriesResult<T> getSeries(String symbol) {
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

   /**
    * Testability method only.
    * 
    * @return the phases list
    */
   public List<String> test__getPhases() {
      return phases;
   }
}
