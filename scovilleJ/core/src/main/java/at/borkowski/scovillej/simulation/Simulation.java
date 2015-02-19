package at.borkowski.scovillej.simulation;

import at.borkowski.scovillej.profile.SeriesResult;

// TODO document this
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

   <T extends Number> SeriesResult<T> getSeries(String symbol);
}
