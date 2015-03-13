package at.borkowski.scovillej.prefetch.members.server;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import at.borkowski.scovillej.simulation.PhaseHandler;
import at.borkowski.scovillej.simulation.Simulation;
import at.borkowski.scovillej.simulation.SimulationContext;
import at.borkowski.scovillej.simulation.SimulationEvent;
import at.borkowski.scovillej.simulation.SimulationInitializationContext;
import at.borkowski.scovillej.simulation.SimulationMember;

/**
 * Represents the server member of the simulation, which provides client members
 * with data. A simple mapping of {@link String} (file name) to
 * <code>byte[]</code> (content) is used.
 */
public class FetchServer implements SimulationMember, PhaseHandler {

   private final SocketProcessor socketProcessor;
   private final FileServerProcessor fileServerProcessor;

   private final List<ClientProcessor> clientProcessors = new LinkedList<>();

   /**
    * Creates a new fetch server listening on the given socket name.
    * 
    * @param socketName
    *           the socket name
    */
   public FetchServer(String socketName) {
      this.socketProcessor = new SocketProcessor(this, socketName);
      this.fileServerProcessor = new FileServerProcessor();
   }

   @Override
   public void initialize(Simulation simulation, SimulationInitializationContext context) {
      socketProcessor.initialize(simulation, context);
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

         if (clientProcessors.size() > 0)
            clientProcessors.get((int) (context.getCurrentTick() % clientProcessors.size())).handle(context);

      } catch (Exception ex) {
         throw new RuntimeException(ex);
      }
   }

   @Override
   public Collection<String> getPhaseSubcription() {
      return null;
   }

   /**
    * Adds files to this server member.
    * 
    * @param files
    *           the files to be added.
    */
   public void addFiles(Map<String, byte[]> files) {
      fileServerProcessor.addFiles(files);
   }

   /**
    * Returns the {@link FileServerProcessor} sub-processor.
    * 
    * @return the sub-processor
    */
   public FileServerProcessor getFileServerProcessor() {
      return fileServerProcessor;
   }

   /**
    * Dereigsters a client sub-processor
    * 
    * @param clientHandler
    *           the sub-processor
    */
   public void deregisterClientProcessor(ClientProcessor clientHandler) {
      clientProcessors.remove(clientHandler);
   }

   /**
    * Registers a client sub-processor
    * 
    * @param clientHandler
    *           the sub-processor
    */
   public void registerClientProcessor(ClientProcessor clientHandler) {
      clientProcessors.add(clientHandler);
   }
}
