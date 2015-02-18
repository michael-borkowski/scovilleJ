package org.scovillej.impl.comm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.scovillej.comm.CommunicationService;
import org.scovillej.comm.SimulationServerSocket;
import org.scovillej.comm.SimulationSocket;
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

   public class Exotic {}
}
