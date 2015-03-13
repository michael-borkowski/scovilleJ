package at.borkowski.scovillej.prefetch.members.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import at.borkowski.scovillej.profile.Series;
import at.borkowski.scovillej.services.comm.SimulationSocket;
import at.borkowski.scovillej.simulation.SimulationContext;
import at.borkowski.spicej.WouldBlockException;

public class ClientProcessorTest {

   ClientProcessor sut;

   FetchServer owner;
   SimulationSocket<byte[]> socket;
   FileServerProcessor fileServerProcessor;

   SimulationContext context;

   String request = null;
   byte[] response = null;
   boolean close = false, closed = false;

   long tick = 0;

   @Before
   public void setUp() throws Exception {
      fileServerProcessor = mock(FileServerProcessor.class);
      when(fileServerProcessor.hasFile("file-a")).thenReturn(true);
      when(fileServerProcessor.hasFile("file-b")).thenReturn(false);
      when(fileServerProcessor.getFile("file-a")).thenReturn(new byte[100]);

      socket = new SimulationSocket<byte[]>() {
         @Override
         public void write(byte[] object) throws IOException {
            response = object;
         }

         @Override
         public byte[] read() throws IOException {
            if (close)
               return null;
            if (request == null)
               throw new WouldBlockException();
            byte[] ret = request.getBytes("UTF8");
            request = null;
            return ret;
         }

         @Override
         public boolean established() {
            return true;
         }

         @Override
         public void close() {
            closed = true;
         }

         @Override
         public int available() throws IOException {
            return request == null && !close ? 0 : 1;
         }
      };

      owner = mock(FetchServer.class);
      doReturn(fileServerProcessor).when(owner).getFileServerProcessor();

      sut = new ClientProcessor(owner, socket);

      context = new SimulationContext() {
         @Override
         public <T> T getService(Class<T> clazz) {
            return null;
         }

         @Override
         public <T> Series<T> getSeries(String symbol, Class<T> clazz) {
            return null;
         }

         @Override
         public long getCurrentTick() {
            return tick;
         }

         @Override
         public String getCurrentPhase() {
            return "tick";
         }
      };
   }

   @Test
   public void test() throws IOException {
      advance();
      advance();
      advance();

      request = "file-a";

      advance();

      assertNull(request);
      assertEquals(100, response.length);

      advance();
      advance();

      request = "file-b";

      advance();

      assertNull(request);
      assertNull(request);

      close = true;
      
      advance();

      assertTrue(closed);
      verify(owner).deregisterClientProcessor(sut);

   }

   private void advance() throws IOException {
      sut.handle(context);
      tick++;
   }

}
