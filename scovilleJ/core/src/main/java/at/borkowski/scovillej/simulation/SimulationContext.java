package at.borkowski.scovillej.simulation;

import at.borkowski.scovillej.profile.Series;

/**
 * Represents the simulation context to be used by simulation members in order
 * to access functionality outside the simulation (like series) and services, to
 * communicate with other simulation members.
 *
 */
public interface SimulationContext {
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

   /**
    * Returns a series object which can be used by the calling member to record
    * profiling numbers. The given object will be usable throughout the whole
    * simulation, ie. it can be stored by the caller for subsequent uses.
    * 
    * If the series is not known to the simulation (not defined at creation
    * time) or has the wrong type <code>T</code>, <code>null</code> is returned.
    * 
    * @param symbol
    *           the symbol (name of series) to record
    * @param clazz
    *           the runtime class object for <code>T</code>
    * @return the series objects
    */
   <T extends Number> Series<T> getSeries(String symbol, Class<T> clazz);

   /**
    * Returns a series object for a given class. If the series is not provided
    * by any series provider, <code>null</code> is returned.
    * 
    * @param clazz
    *           the type of service requests
    * @return the service object, or <code>null</code> if no service is
    *         providing this interface
    */
   <T> T getService(Class<T> clazz);
}
