package org.scovillej.simulation;

import java.util.Collection;

public interface SimulationMember {
   Collection<SimulationEvent> generateEvents();

   String getName();
}
