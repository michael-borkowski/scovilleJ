package at.borkowski.scovillej.prefetch.members.client;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import at.borkowski.scovillej.prefetch.Request;
import at.borkowski.scovillej.prefetch.algorithms.PrefetchAlgorithm;
import at.borkowski.scovillej.prefetch.profiling.PrefetchProfilingService;
import at.borkowski.scovillej.profile.Series;
import at.borkowski.scovillej.simulation.SimulationContext;

public class FetchProcessorTest {

   FetchClient owner;
   FetchProcessor sut;

   Request[] requests;

   SocketProcessor socketProcessor;
   PrefetchProfilingService profilingService;
   CacheProcessor cacheProcessor;

   SimulationContext context;
   long tick = 0;

   byte[] data = null;

   @Before
   public void setUp() throws Exception {
      socketProcessor = mock(SocketProcessor.class);
      profilingService = mock(PrefetchProfilingService.class);
      cacheProcessor = mock(CacheProcessor.class);

      owner = mock(FetchClient.class);

      when(owner.getCacheProcessor()).thenReturn(cacheProcessor);
      when(owner.getSocketProcessor()).thenReturn(socketProcessor);
      when(owner.getProfilingService()).thenReturn(profilingService);
      when(socketProcessor.readIfPossible()).then(returnData());

      sut = new FetchProcessor(owner);
      sut.setAlgorithm(new PrefetchAlgorithm() {

         @Override
         public Map<Long, Request> schedule(Set<Request> requests) {
            Map<Long, Request> ret = new HashMap<>();
            for (Request req : requests)
               ret.put(req.getDeadline() + req.getData(), req);
            return ret;
         }
      });

      requests = new Request[] { new Request(100, 10, 1, "file-1"), new Request(200, 13, 1, "file-2") };

      sut.addRequests(Arrays.asList(requests));
      sut.initialize(null, null);

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

   private Answer<byte[]> returnData() {
      return new Answer<byte[]>() {
         public byte[] answer(InvocationOnMock invocation) throws Throwable {
            return data;
         }
      };
   }

   private void subTestRequest0() throws IOException {
      // request 0

      advanceUntil(110);

      verify(socketProcessor, never()).request(anyString());
      verify(socketProcessor, never()).readIfPossible();

      advance();

      verify(socketProcessor).request("file-1");
      verify(socketProcessor, never()).readIfPossible();

      advance();

      verify(socketProcessor).readIfPossible();

      advance();
      advance();

      verify(profilingService, never()).fetched(any(Request.class), anyInt(), anyLong(), anyLong());
      verify(cacheProcessor, never()).save(anyString(), any(byte[].class), anyLong());

      data = new byte[11];

      advance();

      verify(profilingService).fetched(requests[0], 11, 114, 4);
      verify(cacheProcessor).save("file-1", data, 114);

      data = null;
   }

   private void subTestRequest1() throws IOException {
      // request 1

      advanceUntil(213);

      verify(socketProcessor, never()).request("file-2");

      advance();

      verify(socketProcessor).request("file-2");

      advance();
      advance();
      advance();
      advance();
      advance();

      verify(profilingService, never()).fetched(same(requests[1]), anyInt(), anyLong(), anyLong());
      verify(cacheProcessor, never()).save(eq("file-2"), any(byte[].class), anyLong());

      data = new byte[37];

      advance();

      verify(profilingService).fetched(requests[1], 37, 219, 6);
      verify(cacheProcessor).save("file-2", data, 219);
   }

   @Test
   public void test() throws IOException {
      subTestRequest0();
      subTestRequest1();
   }

   @Test
   public void testUrge() throws IOException {
      subTestRequest0();
      
      sut.urge(0, requests[1]);
      
      verify(socketProcessor, never()).request("file-2");

      advance();

      verify(socketProcessor).request("file-2");

      advance();
      advance();
      advance();
      advance();
      advance();

      verify(profilingService, never()).fetched(same(requests[1]), anyInt(), anyLong(), anyLong());
      verify(cacheProcessor, never()).save(eq("file-2"), any(byte[].class), anyLong());

      data = new byte[37];

      advance();

      verify(profilingService).fetched(requests[1], 37, 121, 6);
      verify(cacheProcessor).save("file-2", data, 121);
   }

   private void advanceUntil(int tick) throws IOException {
      while (this.tick < tick)
         advance();
   }

   private void advance() throws IOException {
      sut.executePhase(context);
      tick++;
   }

}