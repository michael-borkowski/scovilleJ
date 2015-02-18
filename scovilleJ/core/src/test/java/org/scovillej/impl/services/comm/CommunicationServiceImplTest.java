package org.scovillej.impl.services.comm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.scovillej.impl.services.comm.CommunicationServiceImpl;
import org.scovillej.services.comm.CommunicationService;
import org.scovillej.services.comm.SimulationServerSocket;
import org.scovillej.services.comm.SimulationSocket;
import org.spicej.impl.SimulationTickSource;

public class CommunicationServiceImplTest {

   SimulationTickSource t;
   CommunicationService sut;

   @Before
   public void setUp() {
      t = new SimulationTickSource();
      sut = new CommunicationServiceImpl(t);
   }

   @Test(expected = IOException.class)
   public void testNonExistingName() throws IOException {
      sut.beginConnect("non-existing");
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

      SimulationSocket<String> socketA = sut.beginConnect("accepter");
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
   }

   @Test
   public void testUplinkRatedConversation() throws IOException {
      sut.setRates("accepter", 2, null);

      SimulationServerSocket<String> serverSocket = sut.createServerSocket("accepter", String.class);
      assertEquals(0, serverSocket.available());
      assertNull(serverSocket.accept());

      SimulationSocket<String> socketA = sut.beginConnect("accepter");
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
      advance(5, socketA, socketB);
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
      advance(10, socketA, socketB);
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
   }

   @Test
   public void testDownlinkRatedConversation() throws IOException {
      sut.setRates("accepter", null, 3);

      SimulationServerSocket<String> serverSocket = sut.createServerSocket("accepter", String.class);
      assertEquals(0, serverSocket.available());
      assertNull(serverSocket.accept());

      SimulationSocket<String> socketB = sut.beginConnect("accepter");
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
      advance(3, socketA, socketB);
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
      advance(6, socketA, socketB);
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
   }

   @Test
   public void testBothRatedConversation() throws IOException {
      sut.setRates("accepter", 1, 3);

      SimulationServerSocket<String> serverSocket = sut.createServerSocket("accepter", String.class);
      assertEquals(0, serverSocket.available());
      assertNull(serverSocket.accept());

      SimulationSocket<String> socketB = sut.beginConnect("accepter");
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
      advance(3, socketA, socketB);
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
      advance(14, socketA, socketB);
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
      advance(6, socketA, socketB);
      assertEquals(1, socketA.available());
      assertEquals(0, socketB.available());
      advance(1);
      assertEquals(1, socketA.available());
      assertEquals(1, socketB.available());

      socketB.write("funny."); // 4 + 6 = 10 bytes = 10 ticks (9 advances)

      assertEquals(1, socketA.available());
      assertEquals(1, socketB.available());
      advance(8, socketA, socketB);
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
   }

   private void advance(int count, SimulationSocket<?>... sockets) throws IOException {
      for (int i = 0; i < count; i++) {
         t.advance();
         for (SimulationSocket<?> socket : sockets)
            socket.available();
      }
   }

   public class Exotic {}
}
