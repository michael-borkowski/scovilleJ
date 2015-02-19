package at.borkowski.scovillej.simulation;

// TODO document this
public interface SimulationEvent extends PhaseHandler {
   SimulationMember getMember();

   long getScheduledTick();

   String getScheduledPhase();
}
