package org.scovillej.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.scovillej.impl.series.LongSeriesImpl;
import org.scovillej.simulation.Simulation;

public class LongSeriesImplTest {

   private static final double EPSILON = 0.000000000000001;

   LongSeriesImpl sut;
   private long tick;

   @Before
   public void setUp() {
      Simulation sim = mock(Simulation.class);
      when(sim.getCurrentTick()).then(returnCurrentTick());

      sut = new LongSeriesImpl();
      sut.initialize(sim, 1000);
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
      sut.measure(10L);
      tick = 1;
      sut.measure(12L);
      tick = 2;
      sut.measure(13L);
      tick = 3;
      sut.measure(18L);

      assertEquals(13.25D, sut.getAverage(), EPSILON);
      assertEquals(4, sut.getCount());
      assertEquals(12.5D, sut.getDoubleMedian(), EPSILON);
      assertFalse(sut.hasExactMedian());
      assertEquals(10L, (long) sut.getMin());
      assertEquals(18L, (long) sut.getMax());
      assertEquals(2.947456530637898992117295937839622356527012485776648871786480, sut.getStandardDeviation(), EPSILON);
   }

   @Test
   public void testMeasures_exactMedian() {
      tick = 0;
      sut.measure(10L);
      tick = 1;
      sut.measure(12L);
      tick = 2;
      sut.measure(13L);
      tick = 3;
      sut.measure(18L);
      tick = 4;
      sut.measure(24L);

      assertEquals(15.4D, sut.getAverage(), EPSILON);
      assertEquals(5, sut.getCount());
      assertEquals(13D, sut.getDoubleMedian(), EPSILON);
      assertEquals(13L, sut.getNativeMedian(), EPSILON);
      assertTrue(sut.hasExactMedian());
      assertEquals(10L, (long) sut.getMin());
      assertEquals(24L, (long) sut.getMax());
      assertEquals(5.043808085167396612491450333813244919891243650358163750978986D, sut.getStandardDeviation(), EPSILON);
   }

   @Test
   public void testAll() {
      tick = 50;
      sut.measure(20L);
      tick = 51;
      sut.measure(22L);
      tick = 52;
      sut.measure(23L);
      tick = 53;
      sut.measure(30L);

      Map<Long, Long> all = sut.getAll();
      assertEquals(20L, all.get(50L), EPSILON);
      assertEquals(22L, all.get(51L), EPSILON);
      assertEquals(23L, all.get(52L), EPSILON);
      assertEquals(30L, all.get(53L), EPSILON);
      assertEquals(4, all.size());
   }

   @Test
   public void testAveraged() {
      tick = 0;
      sut.measure(10L);
      tick = 1;
      sut.measure(12L);
      tick = 2;
      sut.measure(13L);
      tick = 3;
      sut.measure(18L);

      tick = 10;
      sut.measure(50L);
      tick = 11;
      sut.measure(52L);
      tick = 12;
      sut.measure(53L);
      tick = 13;
      sut.measure(60L);

      tick = 50;
      sut.measure(20L);
      tick = 51;
      sut.measure(22L);
      tick = 52;
      sut.measure(23L);
      tick = 53;
      sut.measure(29L);

      Map<Long, Double> averaged = sut.getAveraged(10);
      assertEquals(13.25D, averaged.get(0L), EPSILON);
      assertEquals(53.75D, averaged.get(10L), EPSILON);
      assertEquals(23.5D, averaged.get(50L), EPSILON);
      assertEquals(100, sut.getAveraged(10).size());
   }

   public void testMedians() {
      tick = 0;
      assertFalse(sut.hasExactMedian());

      tick++;
      sut.measure(1L);
      assertTrue(sut.hasExactMedian());

      tick++;
      sut.measure(1L);
      assertFalse(sut.hasExactMedian());

      tick++;
      sut.measure(1L);
      assertTrue(sut.hasExactMedian());

      tick++;
      sut.measure(1L);
      assertFalse(sut.hasExactMedian());

      tick++;
      sut.measure(1L);
      assertTrue(sut.hasExactMedian());
   }

   @Test
   public void testEmpty() {
      assertEquals(0, sut.getAll().size());
      assertEquals(100, sut.getAveraged(10).size());
      assertNull(sut.getAverage());
      assertEquals(0, sut.getCount());
      assertEquals(null, sut.getDoubleMedian());
      assertFalse(sut.hasExactMedian());
      assertEquals(null, sut.getNativeMedian());
      assertEquals(null, sut.getMax());
      assertEquals(null, sut.getMin());
      assertEquals(null, sut.getStandardDeviation());
   }
}
