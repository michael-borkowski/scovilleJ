package at.borkowski.scovillej.impl;

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

import at.borkowski.scovillej.impl.series.IntegerSeriesImpl;
import at.borkowski.scovillej.simulation.Simulation;

public class IntegerSeriesImplTest {

   private static final double EPSILON = 0.000000000000001;

   IntegerSeriesImpl sut;
   private long tick;

   @Before
   public void setUp() {
      Simulation sim = mock(Simulation.class);
      when(sim.getTotalTicks()).thenReturn((long) 1000);
      when(sim.getCurrentTick()).then(returnCurrentTick());

      sut = new IntegerSeriesImpl();
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
      sut.measure(10);
      tick = 1;
      sut.measure(12);
      tick = 2;
      sut.measure(13);
      tick = 3;
      sut.measure(18);

      assertEquals(13.25D, sut.getAverage(), EPSILON);
      assertEquals(4, sut.getCount());
      assertEquals(12.5D, sut.getDoubleMedian(), EPSILON);
      assertFalse(sut.hasSingleMedian());
      assertEquals(10, (int) sut.getMin());
      assertEquals(18, (int) sut.getMax());
      assertEquals(2.947456530637898992117295937839622356527012485776648871786480, sut.getStandardDeviation(), EPSILON);
   }

   @Test
   public void testMeasures_exactMedian() {
      tick = 0;
      sut.measure(10);
      tick = 1;
      sut.measure(12);
      tick = 2;
      sut.measure(13);
      tick = 3;
      sut.measure(18);
      tick = 4;
      sut.measure(24);

      assertEquals(15.4D, sut.getAverage(), EPSILON);
      assertEquals(5, sut.getCount());
      assertEquals(13D, sut.getDoubleMedian(), EPSILON);
      assertEquals(13, (int) sut.getNativeMedian());
      assertTrue(sut.hasSingleMedian());
      assertEquals(10, (int) sut.getMin());
      assertEquals(24, (int) sut.getMax());
      assertEquals(5.043808085167396612491450333813244919891243650358163750978986D, sut.getStandardDeviation(), EPSILON);
   }

   @Test
   public void testAll() {
      tick = 50;
      sut.measure(20);
      tick = 51;
      sut.measure(22);
      tick = 52;
      sut.measure(23);
      tick = 53;
      sut.measure(30);

      Map<Long, Integer> all = sut.getAll();
      assertEquals(20, all.get(50L), EPSILON);
      assertEquals(22, all.get(51L), EPSILON);
      assertEquals(23, all.get(52L), EPSILON);
      assertEquals(30, all.get(53L), EPSILON);
      assertEquals(4, all.size());
   }

   @Test
   public void testAveraged() {
      tick = 0;
      sut.measure(10);
      tick = 1;
      sut.measure(12);
      tick = 2;
      sut.measure(13);
      tick = 3;
      sut.measure(18);

      tick = 10;
      sut.measure(50);
      tick = 11;
      sut.measure(52);
      tick = 12;
      sut.measure(53);
      tick = 13;
      sut.measure(60);

      tick = 50;
      sut.measure(20);
      tick = 51;
      sut.measure(22);
      tick = 52;
      sut.measure(23);
      tick = 53;
      sut.measure(29);

      Map<Long, Double> averaged = sut.getAveraged(10);
      assertEquals(13.25D, averaged.get(0L), EPSILON);
      assertEquals(53.75D, averaged.get(10L), EPSILON);
      assertEquals(23.5D, averaged.get(50L), EPSILON);
      assertEquals(100, sut.getAveraged(10).size());
   }

   public void testMedians() {
      tick = 0;
      assertFalse(sut.hasSingleMedian());

      tick++;
      sut.measure(1);
      assertTrue(sut.hasSingleMedian());

      tick++;
      sut.measure(1);
      assertFalse(sut.hasSingleMedian());

      tick++;
      sut.measure(1);
      assertTrue(sut.hasSingleMedian());

      tick++;
      sut.measure(1);
      assertFalse(sut.hasSingleMedian());

      tick++;
      sut.measure(1);
      assertTrue(sut.hasSingleMedian());
   }

   @Test
   public void testEmpty() {
      assertEquals(0, sut.getAll().size());
      assertEquals(100, sut.getAveraged(10).size());
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
