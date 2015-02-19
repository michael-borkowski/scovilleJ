package at.borkowski.scovillej.services.comm;

import java.io.IOException;
import java.util.Collection;
import java.util.Formatter;

import org.junit.Test;

import at.borkowski.scovillej.SimulationBuilder;
import at.borkowski.scovillej.services.comm.CommunicationBuilder;
import at.borkowski.scovillej.services.comm.CommunicationService;
import at.borkowski.scovillej.services.comm.SimulationServerSocket;
import at.borkowski.scovillej.services.comm.SimulationSocket;
import at.borkowski.scovillej.simulation.Simulation;
import at.borkowski.scovillej.simulation.SimulationContext;
import at.borkowski.scovillej.simulation.SimulationEvent;
import at.borkowski.scovillej.simulation.SimulationMember;

public class CommIntegrationTest {
   @Test
   public void test() {
      SimulationBuilder builder = new SimulationBuilder();
      builder.totalTicks(1000000);
      builder.service(new CommunicationBuilder().communicationPhase("comm").limit("myserver", 4).bufferSize(1024 * 1024).createProvider());
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
         try {
            tick();
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      private void ensureInit() {
         if (init)
            return;
         comm = context.getService(CommunicationService.class);
         try {
            init();
            init = true;
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

      int rx = -1;
      long t0;
      byte[][] files = new byte[4][];
      SimulationSocket<byte[]> clientSocket;

      @Override
      protected void init() throws Exception {
         clientSocket = comm.beginConnect("myserver", byte[].class);
      }

      @SuppressWarnings("resource")
      @Override
      protected void tick() throws IOException {
         if (!clientSocket.established())
            return;

         if (rx >= files.length) {
            clientSocket.close();
            return;
         }

         if (rx == -1) {
            clientSocket.write(new byte[] { (byte) (rx = 0) });
            t0 = context.getCurrentTick();
         }

         while (clientSocket.available() > 0) {
            files[rx] = clientSocket.read();
            int len = files[rx].length;
            t0 = context.getCurrentTick() - t0;
            double rate = (double) len / t0;
            System.out.println("Received " + rx + " (" + files[rx].length + " B, " + new Formatter().format("%.2f", rate) + " B/t) at " + context.getCurrentTick());
            if (rx + 1 < files.length)
               clientSocket.write(new byte[] { (byte) ++rx });
         }
      }
   }
}
