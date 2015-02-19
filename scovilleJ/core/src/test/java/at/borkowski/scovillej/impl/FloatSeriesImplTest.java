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

import at.borkowski.scovillej.impl.series.FloatSeriesImpl;
import at.borkowski.scovillej.simulation.Simulation;

public class FloatSeriesImplTest {

   private static final double EPSILON = 0.00001;

   FloatSeriesImpl sut;
   private long tick;

   @Before
   public void setUp() {
      Simulation sim = mock(Simulation.class);
      when(sim.getCurrentTick()).then(returnCurrentTick());

      sut = new FloatSeriesImpl();
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
      sut.measure(10F);
      tick = 1;
      sut.measure(12F);
      tick = 2;
      sut.measure(13F);
      tick = 3;
      sut.measure(18F);

      assertEquals(13.25D, sut.getAverage(), EPSILON);
      assertEquals(4, sut.getCount());
      assertEquals(12.5D, sut.getDoubleMedian(), EPSILON);
      assertEquals(12.5F, sut.getNativeMedian(), EPSILON);
      assertFalse(sut.hasExactMedian());
      assertEquals(10F, sut.getMin(), EPSILON);
      assertEquals(18F, sut.getMax(), EPSILON);
      assertEquals(2.947456530637898992117295937839622356527012485776648871786480, sut.getStandardDeviation(), EPSILON);
   }

   @Test
   public void testMeasures_exactMedian() {
      tick = 0;
      sut.measure(10F);
      tick = 1;
      sut.measure(12F);
      tick = 2;
      sut.measure(13F);
      tick = 3;
      sut.measure(18F);
      tick = 4;
      sut.measure(24F);

      assertEquals(15.4D, sut.getAverage(), EPSILON);
      assertEquals(5, sut.getCount());
      assertEquals(13D, sut.getDoubleMedian(), EPSILON);
      assertEquals(13F, sut.getNativeMedian(), EPSILON);
      assertTrue(sut.hasExactMedian());
      assertEquals(10F, sut.getMin(), EPSILON);
      assertEquals(24F, sut.getMax(), EPSILON);
      assertEquals(5.043808085167396612491450333813244919891243650358163750978986D, sut.getStandardDeviation(), EPSILON);
   }

   @Test
   public void testAll() {
      tick = 50;
      sut.measure(20F);
      tick = 51;
      sut.measure(22F);
      tick = 52;
      sut.measure(23F);
      tick = 53;
      sut.measure(29.999F);

      Map<Long, Float> all = sut.getAll();
      assertEquals(20F, all.get(50L), EPSILON);
      assertEquals(22F, all.get(51L), EPSILON);
      assertEquals(23F, all.get(52L), EPSILON);
      assertEquals(29.999F, all.get(53L), EPSILON);
      assertEquals(4, all.size());
   }

   @Test
   public void testAveraged() {
      tick = 0;
      sut.measure(10F);
      tick = 1;
      sut.measure(12F);
      tick = 2;
      sut.measure(13F);
      tick = 3;
      sut.measure(18F);

      tick = 10;
      sut.measure(50F);
      tick = 11;
      sut.measure(52F);
      tick = 12;
      sut.measure(53F);
      tick = 13;
      sut.measure(59.999F);

      tick = 50;
      sut.measure(20F);
      tick = 51;
      sut.measure(22F);
      tick = 52;
      sut.measure(23F);
      tick = 53;
      sut.measure(29.999F);

      Map<Long, Double> averaged = sut.getAveraged(10);
      assertEquals(13.25D, averaged.get(0L), EPSILON);
      assertEquals(53.74975D, averaged.get(10L), EPSILON);
      assertEquals(23.74975D, averaged.get(50L), EPSILON);
      assertEquals(100, sut.getAveraged(10).size());
   }

   public void testMedians() {
      tick = 0;
      assertFalse(sut.hasExactMedian());

      tick++;
      sut.measure(1F);
      assertTrue(sut.hasExactMedian());

      tick++;
      sut.measure(1F);
      assertFalse(sut.hasExactMedian());

      tick++;
      sut.measure(1F);
      assertTrue(sut.hasExactMedian());

      tick++;
      sut.measure(1F);
      assertFalse(sut.hasExactMedian());

      tick++;
      sut.measure(1F);
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
