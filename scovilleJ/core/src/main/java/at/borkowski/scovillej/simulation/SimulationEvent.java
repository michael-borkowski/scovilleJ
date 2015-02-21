package at.borkowski.scovillej.simulation;

/**
 * Represents a pre-defined simulation event. Simulation events can be defined
 * by members in order to inject actions into the simulation at a pre-defined
 * time (tick).
 *
 */
public interface SimulationEvent extends PhaseHandler {
   /**
    * Returns the member which is responsible for this event.
    * 
    * @return the owning member
    */
   SimulationMember getMember();

   /**
    * Returns the tick this event is scheduled for
    * 
    * @return
    */
   long getScheduledTick();
}
