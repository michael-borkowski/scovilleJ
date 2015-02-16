package org.scovillej;

public interface Simulation {
   public static final String TICK_PHASE = "tick";

   void executeToEnd();

   void initialize();

   void executeCurrentTick();

   void executeAndIncreaseTick();

   long getCurrentTick();

   void increaseTick();

   void increaseTickStrictly();

   boolean finishedCurrentTick();

   void executeUpToTick(long tick);

   long getTotalTicks();
}
