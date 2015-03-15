package at.borkowski.scovillej.prefetch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import at.borkowski.scovillej.prefetch.algorithms.PrefetchAlgorithm;
import at.borkowski.scovillej.simulation.Simulation;
import at.borkowski.scovillej.simulation.SimulationEvent;

public class PrefetchSimulationBuilderTest {

   PrefetchSimulationBuilder sut;

   PrefetchAlgorithm algorithm;

   HashMap<String, Integer> files = new HashMap<>();
   HashMap<Long, Integer> limits = new HashMap<>();
   Request request0;
   List<Request> requests = new LinkedList<>();
   List<Request> allRequests = new LinkedList<>();

   @Before
   public void setUp() throws Exception {
      algorithm = mock(PrefetchAlgorithm.class);

      sut = new PrefetchSimulationBuilder();

      limits.put(10L, 100);

      request0 = new Request(1, 2, 3, "file1");
      requests.add(new Request(4, 5, 6, "file2"));
      requests.add(new Request(7, 8, 9, "file3"));

      files.put("file1", 10);
      files.put("file2", 20);
      files.put("file3", 30);

      allRequests.add(request0);
      allRequests.addAll(requests);
   }

   @Test
   public void test() {
      sut.algorithm(algorithm);
      sut.files(files);
      sut.limit(13);
      sut.limits(limits);
      sut.request(request0);
      sut.requests(requests);
      sut.totalTicks(10000);

      Simulation simulation = sut.create();

      assertNotNull(sut.getProfiling());

      assertSame(algorithm, sut.test__getFetchClient().getFetchProcessor().getAlgorithm());
      assertEquals(10, sut.test__getFetchServer().getFileServerProcessor().getFileLength("file1"));
      assertEquals(20, sut.test__getFetchServer().getFileServerProcessor().getFileLength("file2"));
      assertEquals(30, sut.test__getFetchServer().getFileServerProcessor().getFileLength("file3"));
      assertEquals(13, sut.test__getCommunicationService().getService().getUplinkRate(sut.test__getSocketName()).intValue());
      assertEquals(13, sut.test__getCommunicationService().getService().getDownlinkRate(sut.test__getSocketName()).intValue());

      Collection<SimulationEvent> rateEvents = sut.test__getRateSetter().generateEvents();
      assertEquals(1, rateEvents.size());
      SimulationEvent event = rateEvents.iterator().next();
      assertEquals(10L, event.getScheduledTick());
      event.executePhase(null);
      assertEquals(100, sut.test__getCommunicationService().getService().getUplinkRate(sut.test__getSocketName()).intValue());
      assertEquals(100, sut.test__getCommunicationService().getService().getDownlinkRate(sut.test__getSocketName()).intValue());

      Request[] pendingRequests = sut.test__getFetchClient().getFetchProcessor().getPendingRequests().toArray(new Request[0]);
      assertEquals(allRequests.size(), pendingRequests.length);
      for (Request expected : allRequests) {
         boolean found = false;
         for (Request r : pendingRequests)
            if (expected == r)
               found = true;
         assertTrue(found);
      }
      assertEquals(10000, simulation.getTotalTicks());
   }
}
