package at.borkowski.scovillej.prefetch.members.client;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import at.borkowski.scovillej.services.comm.CommunicationService;
import at.borkowski.scovillej.services.comm.SimulationSocket;
import at.borkowski.scovillej.simulation.SimulationContext;

public class SocketProcessorTest {

   SocketProcessor sut;
   CommunicationService communicationService;
   SimulationContext context;
   SimulationSocket<byte[]> socket;

   long tick = 0;
   boolean established = false;
   byte[] data = null;

   @SuppressWarnings("unchecked")
   @Before
   public void setUp() throws Exception {
      sut = new SocketProcessor("socket");
      communicationService = mock(CommunicationService.class);
      context = mock(SimulationContext.class);
      socket = mock(SimulationSocket.class);

      when(context.getService(CommunicationService.class)).thenReturn(communicationService);
      when(context.getCurrentTick()).then(returnCurrentTick());

      when(communicationService.beginConnect("socket", byte[].class)).thenReturn(socket);
      when(socket.established()).then(returnEstablished());
      when(socket.available()).then(returnAvailable());
      when(socket.read()).then(returnData());
      doAnswer(writeData()).when(socket).write(any(byte[].class));

      sut.initialize(null, context);
   }

   private Answer<Void> writeData() {
      return new Answer<Void>() {
         @Override
         public Void answer(InvocationOnMock invocation) throws Throwable {
            data = (byte[]) invocation.getArguments()[0];
            return null;
         }
      };
   }

   private Answer<Integer> returnAvailable() {
      return new Answer<Integer>() {
         @Override
         public Integer answer(InvocationOnMock invocation) throws Throwable {
            return data != null ? 1 : 0;
         }
      };
   }

   private Answer<byte[]> returnData() {
      return new Answer<byte[]>() {
         @Override
         public byte[] answer(InvocationOnMock invocation) throws Throwable {
            return data;
         }
      };
   }

   private Answer<Long> returnCurrentTick() {
      return new Answer<Long>() {
         @Override
         public Long answer(InvocationOnMock invocation) throws Throwable {
            return tick;
         }
      };
   }

   private Answer<Boolean> returnEstablished() {
      return new Answer<Boolean>() {
         @Override
         public Boolean answer(InvocationOnMock invocation) throws Throwable {
            return established;
         }
      };
   }

   @Test
   public void testIsReady() throws IOException {
      assertFalse(sut.isReady());

      advance();

      verify(communicationService).beginConnect("socket", byte[].class);

      advance();

      assertFalse(sut.isReady());

      established = true;

      advance();

      assertTrue(sut.isReady());
   }

   @Test
   public void testReadNoData() throws IOException {
      established = true;
      advance();
      advance();
      advance();

      assertNull(sut.readIfPossible());
   }

   @Test
   public void testReadData() throws IOException {
      established = true;
      advance();
      advance();
      advance();

      data = new byte[10];

      assertArrayEquals(data, sut.readIfPossible());
   }

   @Test
   public void testWritedata() throws IOException {
      established = true;
      advance();
      advance();

      sut.request("file");

      assertArrayEquals("file".getBytes("UTF8"), data);
   }

   private void advance() throws IOException {
      sut.executePhase(context);
      tick++;
   }
}
