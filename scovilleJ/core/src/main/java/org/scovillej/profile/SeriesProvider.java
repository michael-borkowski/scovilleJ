package org.scovillej.profile;

import org.scovillej.simulation.Simulation;

public interface SeriesProvider<T extends Number> extends Series<T>, SeriesResult<T> {
   void initialize(Simulation simulation, long totalTicks);
}
