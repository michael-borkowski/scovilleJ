package at.borkowski.scovillej.prefetch.members.server;

import java.io.IOException;

import at.borkowski.scovillej.services.comm.SimulationSocket;
import at.borkowski.scovillej.simulation.SimulationContext;

/**
 * Represents a client sub-processor of {@link FetchServer}. It is responsible
 * for handling communication with one clinet over a {@link SimulationSocket}.
 */
public class ClientProcessor {
   private final FetchServer owner;
   private final SimulationSocket<byte[]> socket;

   /**
    * Creates a new client processor
    * 
    * @param owner
    *           the owning {@link FetchServer}
    * @param socket
    *           the socket to communicate over
    */
   public ClientProcessor(FetchServer owner, SimulationSocket<byte[]> socket) {
      this.owner = owner;
      this.socket = socket;
   }

   /**
    * Handles a phase, ie. performs business logic
    * 
    * @param context
    *           the simulation context
    * @throws IOException
    *            if an I/O exception occurs
    */
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
      if (owner.getFileServerProcessor().hasFile(request))
         socket.write(new byte[owner.getFileServerProcessor().getFileLength(request)]);
      else
         socket.write(null);
   }

   private void close() {
      socket.close();
      owner.deregisterClientProcessor(this);
   }

}
