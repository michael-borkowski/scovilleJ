package at.borkowski.scovillej.simulation;


/**
 * Represents the simulation context to be used by simulation members in order
 * to access functionality of the simulation like series and services, for
 * example to communicate with other simulation members.
 *
 */
public interface SimulationContext extends SimulationInitializationContext {
   /**
    * Returns the current tick
    * 
    * @return the current tick
    */
   long getCurrentTick();

   /**
    * Returns the current phase
    * 
    * @return the current phase
    */
   String getCurrentPhase();
}
