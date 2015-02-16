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
import org.scovillej.simulation.Simulation;
import org.scovillej.simulation.SimulationContext;
import org.scovillej.simulation.SimulationEvent;

public class SimulationImpl implements Simulation {

   private final List<String> phases;
   private final Map<Long, List<SimulationEvent>> tickToEvents;
   private final long totalTicks;
   private final Map<String, SeriesProvider<?>> series;
   private final Set<Object> services;

   private long currentTick = -1;
   private boolean done = false;

   public SimulationImpl(long totalTicks, List<String> phases, Collection<SimulationEvent> collection, Map<String, SeriesProvider<?>> series, Set<Object> services) {
      this.series = series;
      this.totalTicks = totalTicks;
      this.phases = phases;
      this.services = services;

      this.tickToEvents = new HashMap<>();
      for (SimulationEvent event : collection) {
         List<SimulationEvent> list;
         if ((list = tickToEvents.get(event.getScheduledTick())) == null)
            tickToEvents.put(event.getScheduledTick(), list = new LinkedList<SimulationEvent>());
         list.add(event);
      }

      for (SeriesProvider<?> provider : series.values())
         provider.initialize(this, totalTicks);
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
      for (final String phase : phases)
         if (!tickToEvents.containsKey(currentTick))
            continue;
         else
            for (SimulationEvent event : tickToEvents.get(currentTick))
               event.execute(new SimulationContext() {
                  @Override
                  public String getCurrentPhase() {
                     return phase;
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
      done = true;
   }

   @SuppressWarnings("unchecked")
   private <T> T lookup(Class<T> clazz) {
      for (Object o : services)
         if (clazz.isAssignableFrom(o.getClass()))
            return (T) o;
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

   public Map<Long, List<SimulationEvent>> test__getMap() {
      return tickToEvents;
   }

   public List<String> test__getPhases() {
      return phases;
   }
}
