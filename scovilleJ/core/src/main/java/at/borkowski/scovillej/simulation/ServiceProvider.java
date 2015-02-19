package at.borkowski.scovillej.simulation;

import java.util.Collection;

public interface ServiceProvider<T> {
   public Collection<SimulationMember> getMembers();

   public Class<T> getServiceClass();

   public T getService();
}
