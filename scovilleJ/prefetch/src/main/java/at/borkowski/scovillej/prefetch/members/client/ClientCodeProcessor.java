package at.borkowski.scovillej.prefetch.members.client;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import at.borkowski.scovillej.prefetch.Request;
import at.borkowski.scovillej.simulation.SimulationContext;

public class ClientCodeProcessor {

   private final FetchClient owner;

   private Set<Request> required = new HashSet<>();

   public ClientCodeProcessor(FetchClient owner) {
      this.owner = owner;
   }

   public void executePhase(SimulationContext context) {
      long tick = context.getCurrentTick();

      Set<Request> done = new HashSet<>();
      for (Request request : required) {
         if (request.getDeadline() <= tick) {
            if (owner.getCacheProcessor().hasFile(request.getFile())) {
               if (request.getDeadline() == tick)
                  owner.getProfilingService().cacheHit(request, tick - owner.getCacheProcessor().getTimestamp(request.getFile()));
               else
                  owner.getProfilingService().lateArrival(request);
               done.add(request);
            } else {
               owner.getProfilingService().cacheMiss(request);
               owner.getFetchProcessor().urge(tick, request);
            }
         }
      }

      required.removeAll(done);
   }

   public void addRequests(Collection<Request> requests) {
      required.addAll(requests);
   }

}
