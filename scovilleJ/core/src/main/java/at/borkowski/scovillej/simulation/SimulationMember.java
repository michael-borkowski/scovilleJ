package at.borkowski.scovillej.simulation;

import java.util.Collection;

public interface SimulationMember extends PhaseHandler {
   Collection<SimulationEvent> generateEvents();
}
