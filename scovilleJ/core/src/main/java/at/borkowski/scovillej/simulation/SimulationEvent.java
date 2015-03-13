package at.borkowski.scovillej.simulation;

/**
 * Represents a pre-defined simulation event. Simulation events can be defined
 * by members in order to inject actions into the simulation at a pre-defined
 * time (tick).
 *
 */
public interface SimulationEvent extends PhaseHandler {
   /**
    * Returns the tick this event is scheduled for
    * 
    * @return the scheduled tick
    */
   long getScheduledTick();
}
