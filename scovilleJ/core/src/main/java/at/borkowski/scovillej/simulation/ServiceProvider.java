package at.borkowski.scovillej.simulation;


/**
 * Represents a provider for a simulation service.
 *
 * @param <T>
 *           The type of service provided
 */
public interface ServiceProvider<T> extends SimulationMember {

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
