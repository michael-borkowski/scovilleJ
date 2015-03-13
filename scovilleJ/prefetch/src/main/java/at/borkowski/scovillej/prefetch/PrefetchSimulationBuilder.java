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

/**
 * A class facilitating the creation of simulations of prefetching scenarios.
 */
// TODO test this and all classes in this project
public class PrefetchSimulationBuilder {
   private static final String COMM_PHASE = "comm";
   private static final long BASE_DELAY = 2;
   private final FetchClient fetchClient;
   private final FetchServer fetchServer;
   private final PrefetchProfilingServiceImpl profilingService;

   private SimulationBuilder builder = new SimulationBuilder();

   /**
    * Creates a new builder with all parameters set to default.
    */
   public PrefetchSimulationBuilder() {
      builder.phase(Simulation.TICK_PHASE);
      builder.phase(COMM_PHASE);

      CommunicationServiceBuilder commBuilder = new CommunicationServiceBuilder().communicationPhase(COMM_PHASE).delay(FetchServer.SOCKET_NAME, BASE_DELAY).limit(FetchServer.SOCKET_NAME, 30);

      builder.service(commBuilder.create());
      builder.member(fetchServer = new FetchServer());
      builder.member(fetchClient = new FetchClient());
      builder.service(profilingService = new PrefetchProfilingServiceImpl());
   }

   /**
    * Creates the simulation.
    * 
    * @return
    */
   public Simulation create() {
      return builder.create();
   }

   /**
    * Adds a request object to the simulation.
    * 
    * @param request
    *           the request
    * @return this object
    */
   public PrefetchSimulationBuilder request(Request request) {
      fetchClient.addRequests(Arrays.asList(request));
      return this;
   }

   /**
    * Adds requests to the simulation.
    * 
    * @param requests
    *           the requests
    * @return this object
    */
   public PrefetchSimulationBuilder requests(Collection<Request> requests) {
      fetchClient.addRequests(requests);
      return this;
   }

   /**
    * Sets the total number of ticks for this simulation. See
    * {@link SimulationBuilder#totalTicks(long)}.
    * 
    * @param tickCount
    *           the number of total ticks
    * @return this object
    */
   public PrefetchSimulationBuilder totalTicks(long tickCount) {
      builder.totalTicks(tickCount);
      return this;
   }

   /**
    * Adds files which the server member will provide.
    * 
    * @param files
    *           the files as a map from {@link String} (file name) to
    *           <code>byte[]</code> (content)
    * @return this object
    */
   public PrefetchSimulationBuilder files(Map<String, byte[]> files) {
      fetchServer.addFiles(files);
      return this;
   }

   /**
    * Sets the scheduling algorithm.
    * 
    * @param algorithm
    *           the scheduling algorithm
    * @return this object
    */
   public PrefetchSimulationBuilder algorithm(PrefetchAlgorithm algorithm) {
      fetchClient.getFetchProcessor().setAlgorithm(algorithm);
      return this;
   }

   /**
    * Returns the profiling result object.
    * 
    * @return the profiling result object
    */
   public PrefetchProfilingResults getProfiling() {
      return profilingService;
   }
}
