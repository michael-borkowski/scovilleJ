package at.borkowski.scovillej.prefetch.members.aux;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import at.borkowski.scovillej.services.comm.CommunicationService;
import at.borkowski.scovillej.simulation.SimulationEvent;

public class RateSetterTest {

   RateSetter sut;

   CommunicationService communicationService;

   @Before
   public void setUp() throws Exception {
      communicationService = mock(CommunicationService.class);
   }

   @Test
   public void test() {
      Map<Long, Integer> limits = new HashMap<>();

      limits.put(0L, 30);
      limits.put(5L, 80);
      limits.put(10L, Integer.MAX_VALUE);
      limits.put(15L, 1);
      limits.put(20L, 0);
      limits.put(25L, 10);
      limits.put(25L, null);

      Set<Long> todo = new HashSet<>(limits.keySet());

      sut = new RateSetter("phase", communicationService, "socket", limits);

      sut.initialize(null, null);

      assertNull(sut.getPhaseHandlers());

      Collection<SimulationEvent> events = sut.generateEvents();
      assertEquals(limits.size(), events.size());

      for (SimulationEvent event : events) {
         assertTrue(todo.remove(event.getScheduledTick()));
         assertEquals(1, event.getPhaseSubcription().size());
         assertEquals("phase", event.getPhaseSubcription().toArray()[0]);
         event.executePhase(null);
         verify(communicationService).setRates("socket", limits.get(event.getScheduledTick()), limits.get(event.getScheduledTick()));
      }

      assertTrue(todo.isEmpty());
   }

}
