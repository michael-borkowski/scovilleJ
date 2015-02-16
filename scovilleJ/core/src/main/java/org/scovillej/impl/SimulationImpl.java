package org.scovillej.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.scovillej.Simulation;
import org.scovillej.SimulationContext;
import org.scovillej.SimulationEvent;

public class SimulationImpl implements Simulation {

   private final List<String> phases;
   private final Map<Long, List<SimulationEvent>> tickToEvents;
   private final long totalTicks;

   private long currentTick = -1;
   private boolean done = false;

   public SimulationImpl(List<String> phases, Collection<SimulationEvent> collection, long totalTicks) {
      this.totalTicks = totalTicks;
      this.phases = phases;

      this.tickToEvents = new HashMap<>();
      for (SimulationEvent event : collection) {
         List<SimulationEvent> list;
         if ((list = tickToEvents.get(event.getScheduledTick())) == null)
            tickToEvents.put(event.getScheduledTick(), list = new LinkedList<SimulationEvent>());
         list.add(event);
      }
   }

   @Override
   public void executeToEnd() {
      if (currentTick == -1)
         throw new IllegalStateException("not initialized");
      if (done)
         throw new IllegalStateException("can't re-execute tick");

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
         throw new IllegalStateException("can't re-execute tick");
      if (tick >= totalTicks)
         throw new IllegalArgumentException("tick > tickCount");

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
               });
      done = true;
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

   public Map<Long, List<SimulationEvent>> test__getMap() {
      return tickToEvents;
   }

   public List<String> test__getPhases() {
      return phases;
   }
}
