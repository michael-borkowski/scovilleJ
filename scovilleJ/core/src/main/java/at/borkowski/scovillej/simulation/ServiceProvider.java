package at.borkowski.scovillej.simulation;

import java.util.Collection;

//TODO document this
public interface ServiceProvider<T> {
   public Collection<SimulationMember> getMembers();

   public Class<T> getServiceClass();

   public T getService();
}
