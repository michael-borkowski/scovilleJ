package at.borkowski.scovillej.simulation;

import java.util.Collection;

/**
 * Represents a class handling phase (tick) events.
 *
 */
public interface PhaseHandler {
   /**
    * Returns an array of phases this handler needs to process. This class'
    * {@link #executePhase(SimulationContext)} will be called only for these
    * phases. If this method returns null, it is called only for the default
    * "tick" phase (see {@link Simulation#TICK_PHASE}).
    * 
    * @return the phases reuqired for this phase handler, or null if only the
    *         "tick" phase is required.
    */
   Collection<String> getPhaseSubcription();

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
