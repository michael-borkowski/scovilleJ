package org.scovillej.services.comm;

import java.io.IOException;
import java.util.Collection;

import org.scovillej.SimulationBuilder;
import org.scovillej.simulation.Simulation;
import org.scovillej.simulation.SimulationContext;
import org.scovillej.simulation.SimulationEvent;
import org.scovillej.simulation.SimulationMember;

public class CommIntegrationTest {

   public void test() {
      SimulationBuilder builder = new SimulationBuilder();
      builder.totalTicks(1000);
      builder.service(new CommunicationBuilder().communicationPhase("comm").createProvider());
      builder.member(new Server());
      builder.member(new Client());
      builder.phase(Simulation.TICK_PHASE);
      builder.phase("comm");

      Simulation simulation = builder.create();
      simulation.initialize();

      simulation.executeToEnd();
   }

   private abstract class Member implements SimulationMember {

      private boolean init = false;
      protected SimulationContext context;

      protected CommunicationService comm;

      @Override
      public void executePhase(SimulationContext context) {
         if (!context.getCurrentPhase().equals(Simulation.TICK_PHASE))
            return;

         this.context = context;
         ensureInit();
      }

      private void ensureInit() {
         if (init)
            return;
         comm = context.getService(CommunicationService.class);
         try {
            init();
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      @Override
      public Collection<SimulationEvent> generateEvents() {
         return null;
      }

      protected abstract void init() throws Exception;

      protected abstract void tick() throws Exception;
   }

   private class Server extends Member {
      byte[] file1 = new byte[28];
      byte[] file2 = new byte[314];
      byte[] file3 = new byte[271];
      byte[] file4 = new byte[31337];

      byte[][] files = { file1, file2, file3, file4 };

      SimulationServerSocket<byte[]> serverSocket;
      SimulationSocket<byte[]> clientSocket;

      @Override
      protected void init() throws IOException {
         serverSocket = comm.createServerSocket("myserver", byte[].class);
      }

      @Override
      protected void tick() throws IOException {
         if (clientSocket != null) {
            //if (clientSocket.isClosed())
            //   clientSocket = null;
            //else 
            {
               while (clientSocket.available() > 0) {
                  byte[] msg = clientSocket.read();

                  clientSocket.write(files[msg[0]]);
               }
            }
         } else {
            if (serverSocket.available() > 0)
               clientSocket = serverSocket.accept();
         }
      }
   }

   private class Client extends Member {

      int rx = 0;
      byte[][] files = new byte[4][];
      SimulationSocket<byte[]> clientSocket;

      @Override
      protected void init() throws Exception {
         clientSocket = comm.beginConnect("myserver", byte[].class);
      }

      @Override
      protected void tick() throws IOException {
         if (!clientSocket.established())
            return;

         if (rx >= files.length) {
            clientSocket.close();
            return;
         }

         clientSocket.write(new byte[] { 0 });
         while (clientSocket.available() > 0) {
            files[rx] = clientSocket.read();
            System.out.println("Received " + rx + " at " + context.getCurrentTick());
            clientSocket.write(new byte[] { (byte) rx });
         }
      }
   }
}
