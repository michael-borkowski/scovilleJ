package at.borkowski.scovillej.prefetch.members.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.borkowski.scovillej.services.comm.CommunicationService;
import at.borkowski.scovillej.services.comm.SimulationServerSocket;
import at.borkowski.scovillej.services.comm.SimulationSocket;
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

   private boolean initialized = false;
   private CommunicationService comm;
   private SimulationServerSocket<byte[]> serverSocket;
   private List<ClientHandler> clientHandlers = new ArrayList<>();

   private final String socketName;
   private final Map<String, byte[]> files = new HashMap<>();

   public FetchServer(String socketName) {
      this.socketName = socketName;
   }

   @Override
   public void initialize(Simulation simulation, SimulationInitializationContext context) {}

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
         if (!initialized)
            initialize(context);

         if (serverSocket.available() > 0)
            clientHandlers.add(new ClientHandler(serverSocket.accept()));

         if (clientHandlers.size() > 0)
            clientHandlers.get((int) (context.getCurrentTick() % clientHandlers.size())).handle(context);

      } catch (Exception ex) {
         throw new RuntimeException(ex);
      }
   }

   private void initialize(SimulationContext context) throws IOException {
      comm = context.getService(CommunicationService.class);

      serverSocket = comm.createServerSocket(socketName, byte[].class);

      initialized = true;
   }

   @Override
   public Collection<String> getPhaseSubcription() {
      return null;
   }

   private class ClientHandler {
      private final SimulationSocket<byte[]> socket;

      public ClientHandler(SimulationSocket<byte[]> socket) {
         this.socket = socket;
      }

      public void handle(SimulationContext context) throws IOException {
         if (socket.available() == 0)
            return;
         byte[] requestBytes = socket.read();
         String request = requestBytes == null ? null : new String(requestBytes, "UTF8");

         if (request == null)
            close();
         else
            handle(request, context);
      }

      private void handle(String request, SimulationContext context) throws IOException {
         if (files.containsKey(request))
            socket.write(files.get(request));
         else
            socket.write(null);
      }

      private void close() {
         socket.close();
         clientHandlers.remove(this);
      }

   }

   /**
    * Adds files to this server member.
    * 
    * @param files
    *           the files to be added.
    */
   public void addFiles(Map<String, byte[]> files) {
      this.files.putAll(files);
   }
}
