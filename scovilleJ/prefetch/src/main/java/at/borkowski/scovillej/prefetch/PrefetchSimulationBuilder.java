package at.borkowski.scovillej.prefetch;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import at.borkowski.scovillej.SimulationBuilder;
import at.borkowski.scovillej.prefetch.algorithms.PrefetchAlgorithm;
import at.borkowski.scovillej.prefetch.members.client.FetchClient;
import at.borkowski.scovillej.prefetch.members.server.FetchServer;
import at.borkowski.scovillej.prefetch.profiling.PrefetchProfilingResults;
import at.borkowski.scovillej.prefetch.profiling.PrefetchProfilingServiceImpl;
import at.borkowski.scovillej.services.comm.CommunicationServiceBuilder;
import at.borkowski.scovillej.simulation.Simulation;

public class PrefetchSimulationBuilder {
   private static final String COMM_PHASE = "comm";
   private static final String PROFILING_PHASE = "profiling";
   private static final long BASE_DELAY = 2;
   private final FetchClient fetchClient;
   private final FetchServer fetchServer;
   private final PrefetchProfilingServiceImpl profilingService;

   SimulationBuilder builder = new SimulationBuilder();

   public PrefetchSimulationBuilder() {
      builder.phase(PROFILING_PHASE);
      builder.phase(Simulation.TICK_PHASE);
      builder.phase(COMM_PHASE);

      CommunicationServiceBuilder commBuilder = new CommunicationServiceBuilder().communicationPhase(COMM_PHASE).delay(FetchServer.SOCKET_NAME, BASE_DELAY).limit(FetchServer.SOCKET_NAME, 30);

      builder.service(commBuilder.create());
      builder.member(fetchServer = new FetchServer());
      builder.member(fetchClient = new FetchClient());
      builder.service(profilingService = new PrefetchProfilingServiceImpl(PROFILING_PHASE));
   }

   public Simulation create() {
      return builder.create();
   }

   public PrefetchSimulationBuilder request(Request request) {
      fetchClient.addRequests(Arrays.asList(request));
      return this;
   }

   public PrefetchSimulationBuilder requests(Collection<Request> requests) {
      fetchClient.addRequests(requests);
      return this;
   }

   public PrefetchSimulationBuilder totalTicks(long tickCount) {
      builder.totalTicks(tickCount);
      return this;
   }

   public PrefetchSimulationBuilder files(Map<String, byte[]> files) {
      fetchServer.addFiles(files);
      return this;
   }

   public PrefetchSimulationBuilder algorithm(PrefetchAlgorithm algorithm) {
      fetchClient.setAlgorithm(algorithm);
      return this;
   }

   public PrefetchProfilingResults getProfiling() {
      return profilingService;
   }
}
