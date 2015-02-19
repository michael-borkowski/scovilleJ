package org.scovillej.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scovillej.profile.Series;
import org.scovillej.profile.SeriesProvider;
import org.scovillej.profile.SeriesResult;
import org.scovillej.simulation.PhaseHandler;
import org.scovillej.simulation.ServiceProvider;
import org.scovillej.simulation.Simulation;
import org.scovillej.simulation.SimulationContext;
import org.scovillej.simulation.SimulationEvent;
import org.scovillej.simulation.SimulationMember;

public class SimulationImpl implements Simulation {

   private final long totalTicks;
   private final List<String> phases;
   private final Collection<SimulationMember> members = new LinkedList<>();
   private final Map<Long, Map<String, List<SimulationEvent>>> phaseToEvents;
   private final Map<String, SeriesProvider<?>> series;
   private final Set<ServiceProvider<?>> services;

   private long currentTick = -1;
   private boolean done = false;

   public SimulationImpl(long totalTicks, List<String> phases, List<SimulationMember> members, Collection<SimulationEvent> events, Map<String, SeriesProvider<?>> series, Set<ServiceProvider<?>> services) {
      this.totalTicks = totalTicks;
      this.phases = phases;
      this.series = series;
      this.services = services;

      this.members.addAll(members);

      this.phaseToEvents = new HashMap<>();
      for (SimulationEvent event : events) {
         Map<String, List<SimulationEvent>> map;
         if ((map = phaseToEvents.get(event.getScheduledTick())) == null)
            phaseToEvents.put(event.getScheduledTick(), map = new HashMap<>());

         List<SimulationEvent> list;
         if ((list = map.get(event.getScheduledPhase())) == null)
            map.put(event.getScheduledPhase(), list = new LinkedList<SimulationEvent>());
         list.add(event);
      }

      for (SeriesProvider<?> provider : series.values())
         provider.initialize(this, totalTicks);

      for (ServiceProvider<?> service : services)
         this.members.addAll(service.getMembers());
   }

   @Override
   public void executeToEnd() {
      if (currentTick == -1)
         throw new IllegalStateException("not initialized");
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
      if (currentTick == -1)
         throw new IllegalStateException("not initialized");
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
         if (phaseToEvents.containsKey(currentTick) && phaseToEvents.get(currentTick).containsKey(currentPhase))
            handleEventPhase(currentPhase, phaseToEvents.get(currentTick).get(currentPhase));

         handleMemberPhase(currentPhase, members);
      }

      done = true;
   }

   private void handleEventPhase(String currentPhase, Collection<SimulationEvent> events) {
      for (SimulationEvent event : events)
         handlePhase(currentPhase, event);
   }

   private void handleMemberPhase(String currentPhase, Collection<SimulationMember> members) {
      for (SimulationMember member : members)
         handlePhase(currentPhase, member);
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
         public <T extends Number> Series<T> getSeries(String symbol) {
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
   public void initialize() {
      if (currentTick != -1)
         throw new IllegalStateException("already initialized");

      currentTick = 0;
   }

   @Override
   public void executeCurrentTick() {
      if (currentTick == -1)
         throw new IllegalStateException("not initialized");
      if (done)
         throw new IllegalStateException("can't re-execute tick");
      executeTick();
   }

   @Override
   public long getCurrentTick() {
      if (currentTick == -1)
         throw new IllegalStateException("not initialized");
      return currentTick;
   }

   @Override
   public void executeAndIncreaseTick() {
      if (currentTick == -1)
         throw new IllegalStateException("not initialized");
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
      if (currentTick == -1)
         throw new IllegalStateException("not initialized");
      if (!done)
         throw new IllegalStateException("can't skip tick");

      if (currentTick + 1 < totalTicks) {
         currentTick++;
         done = false;
      } else if (strict)
         throw new IllegalStateException("can't advance past last tick");
   }

   @Override
   public boolean finishedCurrentTick() {
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

   public Map<Long, Map<String, List<SimulationEvent>>> test__getMap() {
      return phaseToEvents;
   }

   public List<String> test__getPhases() {
      return phases;
   }
}
