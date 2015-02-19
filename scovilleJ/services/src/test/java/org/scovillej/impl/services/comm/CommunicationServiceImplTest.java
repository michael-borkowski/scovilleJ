package org.scovillej.impl.services.comm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.scovillej.profile.Series;
import org.scovillej.services.comm.SimulationServerSocket;
import org.scovillej.services.comm.SimulationSocket;
import org.scovillej.simulation.Simulation;
import org.scovillej.simulation.SimulationContext;

public class CommunicationServiceImplTest {

   CommunicationServiceImpl sut;

   @Before
   public void setUp() {
      sut = new CommunicationServiceImpl();
   }

   @Test(expected = IOException.class)
   public void testNonExistingName() throws IOException {
      sut.beginConnect("non-existing", Void.class);
   }

   @Test(expected = IOException.class)
   public void testIncompatibleType() throws IOException {
      sut.createServerSocket("incompatible", String.class);
      sut.beginConnect("incompatible", byte[].class);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testWrongType() throws IOException {
      sut.createServerSocket("exotic", Exotic.class);
   }

   @Test
   public void testDuplicate() throws IOException {
      sut.createServerSocket("existing", String.class);

      try {
         sut.createServerSocket("existing", String.class);
         fail();
      } catch (IOException expected) {}
   }

   @Test
   public void testConversation() throws IOException {
      sut.setRates("accepter", null, null);

      SimulationServerSocket<String> serverSocket = sut.createServerSocket("accepter", String.class);
      assertEquals(0, serverSocket.available());
      assertNull(serverSocket.accept());

      SimulationSocket<String> socketA = sut.beginConnect("accepter", String.class);
      assertFalse(socketA.established());
      assertEquals(1, serverSocket.available());

      SimulationSocket<String> socketB = serverSocket.accept();
      assertTrue(socketA.established());
      assertTrue(socketB.established());

      assertEquals(0, socketA.available());
      assertEquals(0, socketB.available());

      ////////////////////////////////////////

      socketA.write("hey there!");

      assertEquals(0, socketA.available());
      assertEquals(1, socketB.available());

      assertEquals("hey there!", socketB.read());

      assertEquals(0, socketA.available());
      assertEquals(0, socketB.available());

      ////////////////////////////////////////

      socketB.write("how are you?");

      assertEquals(1, socketA.available());
      assertEquals(0, socketB.available());

      socketA.write("what are you doing?");

      assertEquals(1, socketA.available());
      assertEquals(1, socketB.available());

      socketB.write("funny.");

      assertEquals(2, socketA.available());
      assertEquals(1, socketB.available());

      ////////////////////////////////////////

      assertEquals("how are you?", socketA.read());
      assertEquals("funny.", socketA.read());

      assertEquals("what are you doing?", socketB.read());

      assertEquals(0, socketA.available());
      assertEquals(0, socketB.available());

      socketA.close();
      socketB.close();
   }

   @Test
   public void testUplinkRatedConversation() throws IOException {
      sut.setRates("accepter", 2, null);

      SimulationServerSocket<String> serverSocket = sut.createServerSocket("accepter", String.class);
      assertEquals(0, serverSocket.available());
      assertNull(serverSocket.accept());

      SimulationSocket<String> socketA = sut.beginConnect("accepter", String.class);
      assertFalse(socketA.established());
      assertEquals(1, serverSocket.available());

      SimulationSocket<String> socketB = serverSocket.accept();
      assertTrue(socketA.established());
      assertTrue(socketB.established());

      assertEquals(0, socketA.available());
      assertEquals(0, socketB.available());

      ////////////////////////////////////////
      advance(10);

      socketA.write("hey there!"); // 4 + 10 = 14 bytes = 7 ticks (6 advances)

      assertEquals(0, socketA.available());
      assertEquals(0, socketB.available());
      advance(5);
      assertEquals(0, socketA.available());
      assertEquals(0, socketB.available());
      advance(1);
      assertEquals(0, socketA.available());
      assertEquals(1, socketB.available());

      assertEquals("hey there!", socketB.read());

      assertEquals(0, socketA.available());
      assertEquals(0, socketB.available());

      ////////////////////////////////////////
      advance(10);

      socketB.write("how are you?"); // available immediately (no shaping)

      assertEquals(1, socketA.available());
      assertEquals(0, socketB.available());

      ////////////////////////////////////////
      advance(10);

      assertEquals(1, socketA.available());
      assertEquals(0, socketB.available());

      socketA.write("what are you doing?"); // 4 + 19 = 23 bytes = 12 ticks (11 advances)

      assertEquals(1, socketA.available());
      assertEquals(0, socketB.available());
      advance(10);
      assertEquals(1, socketA.available());
      assertEquals(0, socketB.available());
      advance(1);
      assertEquals(1, socketA.available());
      assertEquals(1, socketB.available());

      socketB.write("funny."); // available immediately (no shaping)

      assertEquals(2, socketA.available());
      assertEquals(1, socketB.available());

      ////////////////////////////////////////

      assertEquals("how are you?", socketA.read());
      assertEquals("funny.", socketA.read());

      assertEquals("what are you doing?", socketB.read());

      assertEquals(0, socketA.available());
      assertEquals(0, socketB.available());

      socketA.close();
      socketB.close();
   }

   @Test
   public void testDownlinkRatedConversation() throws IOException {
      sut.setRates("accepter", null, 3);

      SimulationServerSocket<String> serverSocket = sut.createServerSocket("accepter", String.class);
      assertEquals(0, serverSocket.available());
      assertNull(serverSocket.accept());

      SimulationSocket<String> socketB = sut.beginConnect("accepter", String.class);
      assertFalse(socketB.established());
      assertEquals(1, serverSocket.available());

      SimulationSocket<String> socketA = serverSocket.accept();
      assertTrue(socketA.established());
      assertTrue(socketB.established());

      assertEquals(0, socketA.available());
      assertEquals(0, socketB.available());

      ////////////////////////////////////////
      advance(10);

      socketA.write("hey there!"); // 4 + 10 = 14 bytes = 5 ticks (4 advances)

      assertEquals(0, socketA.available());
      assertEquals(0, socketB.available());
      advance(3);
      assertEquals(0, socketA.available());
      assertEquals(0, socketB.available());
      advance(1);
      assertEquals(0, socketA.available());
      assertEquals(1, socketB.available());

      assertEquals("hey there!", socketB.read());

      assertEquals(0, socketA.available());
      assertEquals(0, socketB.available());

      ////////////////////////////////////////
      advance(10);

      socketB.write("how are you?"); // available immediately (no shaping)

      assertEquals(1, socketA.available());
      assertEquals(0, socketB.available());

      ////////////////////////////////////////
      advance(10);

      assertEquals(1, socketA.available());
      assertEquals(0, socketB.available());

      socketA.write("what are you doing?"); // 4 + 19 = 23 bytes = 8 ticks (7 advances)

      assertEquals(1, socketA.available());
      assertEquals(0, socketB.available());
      advance(6);
      assertEquals(1, socketA.available());
      assertEquals(0, socketB.available());
      advance(1);
      assertEquals(1, socketA.available());
      assertEquals(1, socketB.available());

      socketB.write("funny."); // available immediately (no shaping)

      assertEquals(2, socketA.available());
      assertEquals(1, socketB.available());

      ////////////////////////////////////////

      assertEquals("how are you?", socketA.read());
      assertEquals("funny.", socketA.read());

      assertEquals("what are you doing?", socketB.read());

      assertEquals(0, socketA.available());
      assertEquals(0, socketB.available());

      socketA.close();
      socketB.close();
   }

   @Test
   public void testBothRatedConversation() throws IOException {
      sut.setRates("accepter", 1, 3);

      SimulationServerSocket<String> serverSocket = sut.createServerSocket("accepter", String.class);
      assertEquals(0, serverSocket.available());
      assertNull(serverSocket.accept());

      SimulationSocket<String> socketB = sut.beginConnect("accepter", String.class);
      assertFalse(socketB.established());
      assertEquals(1, serverSocket.available());

      SimulationSocket<String> socketA = serverSocket.accept();
      assertTrue(socketA.established());
      assertTrue(socketB.established());

      assertEquals(0, socketA.available());
      assertEquals(0, socketB.available());

      ////////////////////////////////////////
      advance(10);

      socketA.write("hey there!"); // 4 + 10 = 14 bytes = 5 ticks (4 advances)

      assertEquals(0, socketA.available());
      assertEquals(0, socketB.available());
      advance(3);
      assertEquals(0, socketA.available());
      assertEquals(0, socketB.available());
      advance(1);
      assertEquals(0, socketA.available());
      assertEquals(1, socketB.available());

      assertEquals("hey there!", socketB.read());

      assertEquals(0, socketA.available());
      assertEquals(0, socketB.available());

      ////////////////////////////////////////
      advance(10);

      socketB.write("how are you?"); // 4 + 12 = 16 bytes = 16 ticks (15 advances)

      assertEquals(0, socketA.available());
      assertEquals(0, socketB.available());
      advance(14);
      assertEquals(0, socketA.available());
      assertEquals(0, socketB.available());
      advance(1);
      assertEquals(1, socketA.available());
      assertEquals(0, socketB.available());

      ////////////////////////////////////////
      advance(10);

      assertEquals(1, socketA.available());
      assertEquals(0, socketB.available());

      socketA.write("what are you doing?"); // 4 + 19 = 23 bytes = 8 ticks (7 advances)

      assertEquals(1, socketA.available());
      assertEquals(0, socketB.available());
      advance(6);
      assertEquals(1, socketA.available());
      assertEquals(0, socketB.available());
      advance(1);
      assertEquals(1, socketA.available());
      assertEquals(1, socketB.available());

      socketB.write("funny."); // 4 + 6 = 10 bytes = 10 ticks (9 advances)

      assertEquals(1, socketA.available());
      assertEquals(1, socketB.available());
      advance(8);
      assertEquals(1, socketA.available());
      assertEquals(1, socketB.available());
      advance(1);
      assertEquals(2, socketA.available());
      assertEquals(1, socketB.available());

      ////////////////////////////////////////

      assertEquals("how are you?", socketA.read());
      assertEquals("funny.", socketA.read());

      assertEquals("what are you doing?", socketB.read());

      assertEquals(0, socketA.available());
      assertEquals(0, socketB.available());

      socketA.close();
      socketB.close();
   }

   private void advance(int count) throws IOException {
      for (int i = 0; i < count; i++) {
         sut.executePhase(new SimulationContext() {

            @Override
            public <T> T getService(Class<T> clazz) {
               return null;
            }

            @Override
            public <T extends Number> Series<T> getSeries(String symbol) {
               return null;
            }

            @Override
            public long getCurrentTick() {
               return -1; // not used in client code
            }

            @Override
            public String getCurrentPhase() {
               return Simulation.TICK_PHASE;
            }
         });
      }
   }

   public class Exotic {}
}
