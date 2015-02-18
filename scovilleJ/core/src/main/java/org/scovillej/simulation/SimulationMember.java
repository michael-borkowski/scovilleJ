package org.scovillej.simulation;

import java.util.Collection;

public interface SimulationMember extends PhaseHandler {
   Collection<SimulationEvent> generateEvents();

   String getName();
}
