package at.borkowski.scovillej.simulation;

public interface SimulationEvent extends PhaseHandler {
   SimulationMember getMember();

   long getScheduledTick();

   String getScheduledPhase();
}
