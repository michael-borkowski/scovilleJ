package at.borkowski.scovillej.prefetch.members;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import at.borkowski.scovillej.prefetch.Request;
import at.borkowski.scovillej.prefetch.algorithms.NullAlgorithm;
import at.borkowski.scovillej.prefetch.algorithms.PrefetchAlgorithm;
import at.borkowski.scovillej.prefetch.profiling.PrefetchProfilingService;
import at.borkowski.scovillej.services.comm.CommunicationService;
import at.borkowski.scovillej.services.comm.SimulationSocket;
import at.borkowski.scovillej.simulation.PhaseHandler;
import at.borkowski.scovillej.simulation.Simulation;
import at.borkowski.scovillej.simulation.SimulationContext;
import at.borkowski.scovillej.simulation.SimulationEvent;
import at.borkowski.scovillej.simulation.SimulationInitializationContext;
import at.borkowski.scovillej.simulation.SimulationMember;

public class FetchClient implements SimulationMember, PhaseHandler {

   private Set<Request> toFetch = new HashSet<>();
   private Map<Long, Request> scheduled = new HashMap<>();
   private Map<Long, Request> required = new HashMap<>();
   private Map<String, Long> cache = new HashMap<>();

   private PrefetchAlgorithm algorithm = new NullAlgorithm();

   private boolean initialized = false;
   private CommunicationService comm;
   private PrefetchProfilingService profiling;
   private SimulationSocket<Object> socket;

   private long t0;
   private Request current = null;

   public FetchClient() {}

   @Override
   public void initialize(Simulation simulation, SimulationInitializationContext context) {
      comm = context.getService(CommunicationService.class);
      profiling = context.getService(PrefetchProfilingService.class);
      reschedule();
   }

   @Override
   public Collection<SimulationEvent> generateEvents() {
      return null;
   }

   @Override
   public Collection<PhaseHandler> getPhaseHandlers() {
      return Arrays.asList(this);
   }

   @Override
   public void executePhase(SimulationContext context) {
      long tick = context.getCurrentTick();
      try {
         if (!initialized)
            initialize(context);
         if (!socket.established())
            return;

         Request requiredNow = required.get(tick);
         if (requiredNow != null) {
            if (cache.containsKey(requiredNow.getFile())) {
               profiling.cacheHit(required.remove(tick), tick - cache.get(requiredNow.getFile()));
            } else {
               profiling.cacheMiss(requiredNow);
               scheduled.put(tick, requiredNow);
            }
         }

         if (current != null) {
            if (socket.available() != 0) {
               byte[] payload = (byte[]) socket.read();
               long t = tick - t0;
               profiling.fetched(current);
               cache.put(current.getFile(), tick);
               System.out.printf("%d -              read %s (%d B in %d t, %.2f B/t\n", tick, current.getFile(), payload.length, t, (double) payload.length / t);
               toFetch.remove(current);

               if (current.getDeadline() < tick && required.containsValue(current)) {
                  profiling.lateArrival(current);
                  required.remove(current.getDeadline());
               }

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
               socket.write(current.getFile());
               t0 = tick;
            }
         }
      } catch (Exception ex) {
         throw new RuntimeException(ex);
      }
   }

   private void initialize(SimulationContext context) throws IOException {
      socket = comm.beginConnect(FetchServer.SOCKET_NAME, Object.class);
      initialized = true;
   }

   @Override
   public Collection<String> getPhaseSubcription() {
      return null;
   }

   public void addRequests(Collection<Request> requests) {
      for (Request request : requests) {
         toFetch.add(request);
         required.put(request.getDeadline(), request);
      }
   }

   private void reschedule() {
      scheduled = algorithm.schedule(toFetch);
   }

   public void setAlgorithm(PrefetchAlgorithm algorithm) {
      this.algorithm = algorithm;
   }
}
