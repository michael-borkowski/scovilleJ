package at.borkowski.scovillej.impl.series;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import at.borkowski.scovillej.simulation.Simulation;

public class VoidSeriesImplTest {

   VoidSeriesImpl sut;
   private long tick;

   @Before
   public void setUp() {
      Simulation sim = mock(Simulation.class);
      when(sim.getTotalTicks()).thenReturn((long) 1000);
      when(sim.getCurrentTick()).then(returnCurrentTick());

      sut = new VoidSeriesImpl();
      sut.initialize(sim);
   }

   private Answer<Long> returnCurrentTick() {
      return new Answer<Long>() {
         @Override
         public Long answer(InvocationOnMock invocation) throws Throwable {
            return tick;
         }
      };
   }

   @Test
   public void testMeasures() {
      tick = 0;
      sut.measure(null);
      tick = 1;
      sut.measure(null);
      tick = 2;
      sut.measure(null);
      tick = 3;
      sut.measure(null);

      assertEquals(null, sut.getAverage());
      assertEquals(4, sut.getCount());
      assertEquals(null, sut.getDoubleMedian());
      assertTrue(sut.hasSingleMedian());
      assertEquals(null, sut.getMin());
      assertEquals(null, sut.getMax());
      assertEquals(null, sut.getStandardDeviation());
   }


   @Test
   public void testAll() {
      tick = 50;
      sut.measure(null);
      tick = 51;
      sut.measure(null);
      tick = 52;
      sut.measure(null);
      tick = 53;
      sut.measure(null);

      Map<Long, Void> all = sut.getAll();
      assertTrue(all.containsKey(50L));
      assertTrue(all.containsKey(51L));
      assertTrue(all.containsKey(52L));
      assertTrue(all.containsKey(53L));
      assertEquals(4, all.size());
   }

   @Test
   public void testEmpty() {
      assertEquals(0, sut.getAll().size());
      assertTrue(sut.getAveraged(10).isEmpty());
      assertNull(sut.getAverage());
      assertEquals(0, sut.getCount());
      assertEquals(null, sut.getDoubleMedian());
      assertFalse(sut.hasSingleMedian());
      assertEquals(null, sut.getNativeMedian());
      assertEquals(null, sut.getMax());
      assertEquals(null, sut.getMin());
      assertEquals(null, sut.getStandardDeviation());
   }
}
