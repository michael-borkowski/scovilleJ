package at.borkowski.scovillej.simulation;

import java.util.Collection;

/**
 * Represents a member of a simulation. A member is capable of creating events
 * for ticks, and may also handle ticks on a periodic bases.
 */
// TODO: move PhaseHandler into own method instead of extending it
public interface SimulationMember extends PhaseHandler {
   /**
    * This method is called by the simulation implementation in order to
    * pre-create all events necessary for this member. The method should be
    * called only once, but it should also return the same events (not
    * necessarily a deep copy) upon each call.
    * 
    * @return a collection of events for this member
    */
   Collection<SimulationEvent> generateEvents();
}
