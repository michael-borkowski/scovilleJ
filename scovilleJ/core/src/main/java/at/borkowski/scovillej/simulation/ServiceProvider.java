package at.borkowski.scovillej.simulation;

import java.util.Collection;

/**
 * Represents a provider for a simulation service.
 *
 * @param <T>
 *           The type of service provided
 */
// TODO: make it extend member instead of getRequiredMembers?
public interface ServiceProvider<T> {
   /**
    * Returns a collection of {@link SimulationMember} objects necessary for
    * this service to be provided correctly. The members will be made a regular
    * part of the simulation.
    * 
    * @return a collection of the required simulation members
    */
   public Collection<SimulationMember> getRequiredMembers();

   /**
    * Returns the class object of <code>T</code> to be used for run-time
    * processing.
    * 
    * @return the class object of <code>T</code>
    */
   public Class<T> getServiceClass();

   /**
    * Returns the actual service (handler for interface <code>T</code>) which
    * will be provided to client members upon request.
    * 
    * @return the service <code>T</code>
    */
   public T getService();
}
