package at.borkowski.scovillej.prefetch.members.client;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import at.borkowski.scovillej.prefetch.Request;
import at.borkowski.scovillej.prefetch.algorithms.NullAlgorithm;
import at.borkowski.scovillej.prefetch.algorithms.PrefetchAlgorithm;
import at.borkowski.scovillej.simulation.Simulation;
import at.borkowski.scovillej.simulation.SimulationContext;
import at.borkowski.scovillej.simulation.SimulationInitializationContext;

/**
 * Represents the fetch sub-processor of {@link FetchClient}. It is responsible
 * for fetching requests.
 */
public class FetchProcessor {

   private final FetchClient owner;

   private final Set<Request> toFetch = new HashSet<>();
   private Map<Long, Request> scheduled = new HashMap<>();

   private PrefetchAlgorithm algorithm = new NullAlgorithm();

   private long currentStart;
   private Request current = null;

   public FetchProcessor(FetchClient owner) {
      this.owner = owner;
   }

   public void executePhase(SimulationContext context) throws IOException {
      long tick = context.getCurrentTick();

      if (current != null) {
         byte[] payload = owner.getSocketProcessor().readIfPossible();
         if (payload != null) {
            long duration = tick - currentStart;
            owner.getProfilingService().fetched(current, payload.length, tick, duration);
            owner.getCacheProcessor().save(current.getFile(), payload, tick);

            current = null;
         }
      } else {
         long sel = tick;
         for (long lambda : scheduled.keySet())
            if (lambda <= tick && lambda < sel)
               sel = lambda;

         current = scheduled.remove(sel);

         if (current != null) {
            System.out.printf("%d -              requesting %s (%d, %d)\n", tick, current.getFile(), sel, current.getDeadline());
            owner.getSocketProcessor().request(current.getFile());
            currentStart = tick;
         }
      }
   }

   private void reschedule() {
      scheduled = algorithm.schedule(toFetch);
   }

   public void initialize(Simulation simulation, SimulationInitializationContext context) {
      reschedule();
   }

   public void addRequests(Collection<Request> requests) {
      toFetch.addAll(requests);
   }

   public void setAlgorithm(PrefetchAlgorithm algorithm) {
      this.algorithm = algorithm;
   }

   public void urge(long tick, Request request) {
      while (scheduled.containsKey(tick))
         tick--;
      scheduled.put(tick, request);
   }

   public PrefetchAlgorithm getAlgorithm() {
      return algorithm;
   }

   public Set<Request> getPendingRequests() {
      return toFetch;
   }
}
