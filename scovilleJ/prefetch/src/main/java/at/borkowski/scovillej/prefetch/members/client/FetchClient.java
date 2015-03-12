package at.borkowski.scovillej.prefetch.members.client;

import java.util.Arrays;
import java.util.Collection;

import at.borkowski.scovillej.prefetch.Request;
import at.borkowski.scovillej.prefetch.algorithms.PrefetchAlgorithm;
import at.borkowski.scovillej.prefetch.profiling.PrefetchProfilingService;
import at.borkowski.scovillej.simulation.PhaseHandler;
import at.borkowski.scovillej.simulation.Simulation;
import at.borkowski.scovillej.simulation.SimulationContext;
import at.borkowski.scovillej.simulation.SimulationEvent;
import at.borkowski.scovillej.simulation.SimulationInitializationContext;
import at.borkowski.scovillej.simulation.SimulationMember;

public class FetchClient implements SimulationMember, PhaseHandler {

   private final SocketProcessor socketProcessor;
   private final ClientCodeProcessor clientCodeProcessor;
   private final CacheProcessor cacheProcessor;
   private final FetchProcessor fetchProcessor;

   private PrefetchProfilingService profiling;

   public FetchClient() {
      socketProcessor = new SocketProcessor();
      clientCodeProcessor = new ClientCodeProcessor(this);
      cacheProcessor = new CacheProcessor();
      fetchProcessor = new FetchProcessor(this);
   }

   @Override
   public void initialize(Simulation simulation, SimulationInitializationContext context) {
      socketProcessor.initialize(simulation, context);
      fetchProcessor.initialize(simulation, context);

      profiling = context.getService(PrefetchProfilingService.class);
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
      try {
         socketProcessor.executePhase(context);
         clientCodeProcessor.executePhase(context);
         fetchProcessor.executePhase(context);
      } catch (Exception ex) {
         throw new RuntimeException(ex);
      }
   }

   @Override
   public Collection<String> getPhaseSubcription() {
      return null;
   }

   public void addRequests(Collection<Request> requests) {
      clientCodeProcessor.addRequests(requests);
      fetchProcessor.addRequests(requests);
   }

   public void setAlgorithm(PrefetchAlgorithm algorithm) {
      fetchProcessor.setAlgorithm(algorithm);
   }

   public PrefetchProfilingService getProfilingService() {
      return profiling;
   }

   public CacheProcessor getCacheProcessor() {
      return cacheProcessor;
   }

   public SocketProcessor getSocketProcessor() {
      return socketProcessor;
   }

   public FetchProcessor getFetchProcessor() {
      return fetchProcessor;
   }
}