package at.borkowski.scovillej.simulation;

/**
 * Represents a class handling phase (tick) events.
 *
 */
// TODO: add subscription to certain phases
public interface PhaseHandler {
   /**
    * Called when a phase is being processed. The current phase and tick can be
    * queried in the supplied context parameter.
    * 
    * @param context
    *           The {@link SimulationContext} representing the current state of
    *           the simulation.
    */
   void executePhase(SimulationContext context);
}
