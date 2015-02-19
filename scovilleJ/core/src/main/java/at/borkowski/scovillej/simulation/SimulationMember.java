package at.borkowski.scovillej.simulation;

import java.util.Collection;

// TODO document this
public interface SimulationMember extends PhaseHandler {
   Collection<SimulationEvent> generateEvents();
}
