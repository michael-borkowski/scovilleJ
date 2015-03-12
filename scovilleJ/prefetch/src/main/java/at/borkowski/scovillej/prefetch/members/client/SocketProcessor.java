package at.borkowski.scovillej.prefetch.members.client;

import java.io.IOException;

import at.borkowski.scovillej.prefetch.members.server.FetchServer;
import at.borkowski.scovillej.services.comm.CommunicationService;
import at.borkowski.scovillej.services.comm.SimulationSocket;
import at.borkowski.scovillej.simulation.Simulation;
import at.borkowski.scovillej.simulation.SimulationContext;
import at.borkowski.scovillej.simulation.SimulationInitializationContext;

public class SocketProcessor {
   private CommunicationService comm;
   private SimulationSocket<Object> socket;

   private boolean initialized = false;

   public void initialize(Simulation simulation, SimulationInitializationContext context) {
      comm = context.getService(CommunicationService.class);
   }

   public void executePhase(SimulationContext context) throws IOException {
      if (!initialized)
         initialize(context);
   }

   private void initialize(SimulationContext context) throws IOException {
      socket = comm.beginConnect(FetchServer.SOCKET_NAME, Object.class);
      initialized = true;
   }

   public byte[] readIfPossible() throws IOException {
      if (socket.available() != 0)
         return (byte[]) socket.read();
      return null;
   }

   public void request(String file) throws IOException {
      socket.write(file);
   }
}
