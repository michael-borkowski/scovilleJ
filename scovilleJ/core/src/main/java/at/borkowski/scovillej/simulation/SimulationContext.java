package at.borkowski.scovillej.simulation;

import at.borkowski.scovillej.profile.Series;

public interface SimulationContext {
   long getCurrentTick();

   String getCurrentPhase();

   <T extends Number> Series<T> getSeries(String symbol);

   <T> T getService(Class<T> clazz);
}
