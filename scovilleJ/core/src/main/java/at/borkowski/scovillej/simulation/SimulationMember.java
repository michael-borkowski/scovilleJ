package at.borkowski.scovillej.simulation;

import java.util.Collection;

/**
 * Represents a member of a simulation. A member is capable of creating events
 * for ticks, and may also handle ticks on a periodic bases.
 */
public interface SimulationMember {
   /**
    * Called during the initialization phase of the simulation.
    * 
    * @param simulation
    *           the owning simulation
    * @param context
    *           a context object providing initialization functionality
    */
   void initialize(Simulation simulation, SimulationInitializationContext context);

   /**
    * This method is called by the simulation implementation in order to
    * pre-create all events necessary for this member. The method should be
    * called only once, but it should also return the same events (not
    * necessarily a deep copy) upon each call.
    * 
    * This method may return null instead of an empty list.
    * 
    * @return a collection of events for this member, or null if this member
    *         does not require events
    */
   Collection<SimulationEvent> generateEvents();

   /**
    * Returns a collection of objects which handle phase events for the
    * simulation necessary for this simulation member. This collection may be
    * empty, for example if the member only requires {@link SimulationEvent}
    * objects to function.
    * 
    * This method may return null instead of an empty list.
    * 
    * @return a collection of phase handlers, or null if this member does not
    *         require phase handling.
    */
   Collection<PhaseHandler> getPhaseHandlers();
}
