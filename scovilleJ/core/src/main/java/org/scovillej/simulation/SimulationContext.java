package org.scovillej.simulation;

import org.scovillej.profile.Series;

public interface SimulationContext {
   long getCurrentTick();

   String getCurrentPhase();

   <T extends Number> Series<T> getSeries(String symbol);
}
