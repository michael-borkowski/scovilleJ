package at.borkowski.scovillej.services.comm;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import at.borkowski.scovillej.SimulationBuilder;
import at.borkowski.scovillej.simulation.PhaseHandler;
import at.borkowski.scovillej.simulation.Simulation;
import at.borkowski.scovillej.simulation.SimulationContext;
import at.borkowski.scovillej.simulation.SimulationEvent;
import at.borkowski.scovillej.simulation.SimulationInitializationContext;
import at.borkowski.scovillej.simulation.SimulationMember;

public class CommIntegrationTest {
   @Test
   public void test() {
      SimulationBuilder builder = new SimulationBuilder();
      builder.totalTicks(1000000);
      builder.service(new CommunicationServiceBuilder().communicationPhase("comm").limit("myserver", 4).bufferSize(1024 * 1024).create());
      builder.member(new Server());
      builder.member(new Client());
      builder.phase(Simulation.TICK_PHASE);
      builder.phase("comm");

      Simulation simulation = builder.create();

      simulation.executeToEnd();
   }

   private abstract class Member implements SimulationMember {

      private boolean init = false;
      protected SimulationContext context;

      protected CommunicationService comm;

      @Override
      public Collection<PhaseHandler> getPhaseHandlers() {
         return Arrays.asList(new PhaseHandler() {
            @Override
            public Collection<String> getPhaseSubcription() {
               return Arrays.asList(Simulation.TICK_PHASE);
            }

            @Override
            public void executePhase(SimulationContext context) {
               Member.this.context = context;
               ensureInit();
               try {
                  tick();
               } catch (Exception e) {
                  throw new RuntimeException(e);
               }
            }
         });
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
      public void initialize(Simulation simulation, SimulationInitializationContext context) {}

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
            while (clientSocket.available() > 0) {
               byte[] msg = clientSocket.read();

               clientSocket.write(files[msg[0]]);
            }
         } else {
            if (serverSocket.available() > 0)
               clientSocket = serverSocket.accept();
         }
      }
   }

   private class Client extends Member {

      int rx = -1;
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

         if (rx == -1)
            clientSocket.write(new byte[] { (byte) (rx = 0) });

         while (clientSocket.available() > 0) {
            files[rx] = clientSocket.read();
            if (rx + 1 < files.length)
               clientSocket.write(new byte[] { (byte) ++rx });
         }
      }
   }
}
