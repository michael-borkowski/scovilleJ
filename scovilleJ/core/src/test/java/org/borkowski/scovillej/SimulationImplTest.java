package org.borkowski.scovillej;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.scovillej.impl.DoubleSeriesImpl;
import org.scovillej.impl.SimulationImpl;
import org.scovillej.profile.SeriesProvider;
import org.scovillej.profile.SeriesResult;
import org.scovillej.simulation.Simulation;
import org.scovillej.simulation.SimulationContext;
import org.scovillej.simulation.SimulationEvent;
import org.scovillej.simulation.SimulationMember;

public class SimulationImplTest {

   Simulation sut;

   int totalTicks = 1000;
   String[] phases = { "a", "b", "tick", "c" };
   int[] yesTicks = { 0, 1, 2, 5, 10, 100, 314, 271, 420, 980, 990, 995, 998, 999 };
   int[] noTicks = { -2, -1, 1000, 1001, 1010, 1100 };

   List<String> serviceResults;

   SeriesProvider<Double> sa, sb;

   SimulationMember member;
   Map<Integer, SimulationEvent> tick_evt = new HashMap<>();

   private interface A {
      String x();
   }

   @Before
   public void setUp() {
      member = new SimulationMember() {
         @Override
         public String getName() {
            return "unit-test";
         }

         @Override
         public Collection<SimulationEvent> generateEvents() {
            return null;
         }
      };

      List<String> phaseNames = new LinkedList<>();
      for (String phase : phases)
         phaseNames.add(phase);
      for (int tick : yesTicks)
         tick_evt.put(tick, mockEvent(tick));
      for (int tick : noTicks)
         tick_evt.put(tick, mockEvent(tick));

      Map<String, SeriesProvider<?>> series = new HashMap<>();
      series.put("series-a", sa = new DoubleSeriesImpl());
      series.put("series-b", sb = new DoubleSeriesImpl());

      Set<Object> services = new HashSet<>();
      services.add(new A() {
         @Override
         public String x() {
            return "x";
         }
      });

      serviceResults = new LinkedList<>();

      sut = new SimulationImpl(totalTicks, phaseNames, tick_evt.values(), series, services);
   }

   public void testTotalTicks() {
      assertEquals(totalTicks, sut.getTotalTicks());
   }

   private SimulationEvent mockEvent(final long tick) {
      SimulationEvent event = spy(new SimulationEvent() {

         int phase = 0;

         @Override
         public SimulationMember getMember() {
            return null;
         }

         @Override
         public long getScheduledTick() {
            return tick;
         }

         @Override
         public void execute(SimulationContext context) {
            assertNotEquals(phases.length, phase);

            assertNotNull(context);
            assertEquals(tick, context.getCurrentTick());
            assertEquals(phases[phase], context.getCurrentPhase());

            phase++;

            A service = context.getService(A.class);
            if (service != null) {
               serviceResults.add(context.getCurrentTick() + "-" + context.getCurrentPhase() + "-" + service.x());
            }
         }

      });
      doCallRealMethod().when(event).execute(any(SimulationContext.class));
      return event;
   }

   @Test
   public void testExceptionOnUninitialized() {
      try {
         sut.executeCurrentTick();
         fail();
      } catch (IllegalStateException expected) {}
      try {
         sut.increaseTick();
         fail();
      } catch (IllegalStateException expected) {}
      try {
         sut.executeCurrentTick();
         fail();
      } catch (IllegalStateException expected) {}
      try {
         sut.executeToEnd();
         fail();
      } catch (IllegalStateException expected) {}
      try {
         sut.executeUpToTick(1);
         fail();
      } catch (IllegalStateException expected) {}
      try {
         sut.getCurrentTick();
         fail();
      } catch (IllegalStateException expected) {}
   }

   @Test
   public void testFullExecution() {
      sut.initialize();

      assertEquals(0, sut.getCurrentTick());
      assertFalse(sut.finishedCurrentTick());

      sut.executeToEnd();

      assertEquals(totalTicks - 1, sut.getCurrentTick());
      assertTrue(sut.finishedCurrentTick());

      for (int tick : yesTicks)
         verify(tick_evt.get(tick), times(phases.length)).execute(any(SimulationContext.class));

      for (int tick : noTicks)
         verify(tick_evt.get(tick), never()).execute(any(SimulationContext.class));
   }

   @Test
   public void testFullExecution_executeStrict() {
      sut.initialize();
      assertEquals(0, sut.getCurrentTick());
      assertFalse(sut.finishedCurrentTick());

      for (int i = 0; i < totalTicks; i++) {
         assertEquals(i, sut.getCurrentTick());

         sut.executeCurrentTick();

         assertEquals(i, sut.getCurrentTick());
         assertTrue(sut.finishedCurrentTick());

         if (i + 1 >= totalTicks)
            break;
         sut.increaseTickStrictly();

         assertEquals(i + 1, sut.getCurrentTick());
         assertFalse(sut.finishedCurrentTick());
      }
      assertEquals(totalTicks - 1, sut.getCurrentTick());
      assertTrue(sut.finishedCurrentTick());

      for (int tick : yesTicks)
         verify(tick_evt.get(tick), times(phases.length)).execute(any(SimulationContext.class));

      for (int tick : noTicks)
         verify(tick_evt.get(tick), never()).execute(any(SimulationContext.class));
   }

   @Test
   public void testPartialExecution_execute() {
      int part = 200;

      sut.initialize();
      assertEquals(0, sut.getCurrentTick());
      assertFalse(sut.finishedCurrentTick());

      for (int i = 0; i < part; i++) {
         assertEquals(i, sut.getCurrentTick());

         sut.executeCurrentTick();

         assertEquals(i, sut.getCurrentTick());
         assertTrue(sut.finishedCurrentTick());

         sut.increaseTick();

         assertEquals(i + 1, sut.getCurrentTick());
         assertFalse(sut.finishedCurrentTick());
      }
      assertEquals(part, sut.getCurrentTick());
      assertFalse(sut.finishedCurrentTick());

      for (int tick : yesTicks)
         if (tick < part)
            verify(tick_evt.get(tick), times(phases.length)).execute(any(SimulationContext.class));
         else
            verify(tick_evt.get(tick), never()).execute(any(SimulationContext.class));

      for (int tick : noTicks)
         verify(tick_evt.get(tick), never()).execute(any(SimulationContext.class));
   }

   @Test
   public void testPartialExecution_executeAndIncrease() {
      int part = 200;

      sut.initialize();
      for (int i = 0; i < part; i++)
         sut.executeAndIncreaseTick();
      assertEquals(part, sut.getCurrentTick());
      assertFalse(sut.finishedCurrentTick());

      for (int tick : yesTicks)
         if (tick < part)
            verify(tick_evt.get(tick), times(phases.length)).execute(any(SimulationContext.class));
         else
            verify(tick_evt.get(tick), never()).execute(any(SimulationContext.class));

      for (int tick : noTicks)
         verify(tick_evt.get(tick), never()).execute(any(SimulationContext.class));
   }

   @Test
   public void testSeriesPresent() {
      sut.initialize();
      sut.executeToEnd();
      SeriesResult<Double> seriesA = sut.getSeries("series-a");
      SeriesResult<Double> seriesB = sut.getSeries("series-b");
      assertNotNull(seriesA);
      assertNotNull(seriesB);
   }

   @Test
   public void testSeriesCount() {
      sut.initialize();
      sut.executeUpToTick(10);
      sa.measure(1D);
      sut.executeUpToTick(11);
      sa.measure(1D);
      sb.measure(1D);
      sut.executeUpToTick(12);
      sa.measure(1D);
      sb.measure(1D);
      sut.executeUpToTick(13);
      sb.measure(1D);
      sut.executeUpToTick(14);
      sb.measure(1D);
      sut.executeToEnd();
      SeriesResult<Double> seriesA = sut.getSeries("series-a");
      SeriesResult<Double> seriesB = sut.getSeries("series-b");
      assertNotNull(seriesA);
      assertNotNull(seriesB);

      assertEquals(3, seriesA.getCount());
      assertEquals(4, seriesB.getCount());
   }

   @Test
   public void testServices() {
      sut.initialize();
      sut.executeUpToTick(6);

      String[] expected = { "0-a-x", "0-b-x", "0-tick-x", "0-c-x", "1-a-x", "1-b-x", "1-tick-x", "1-c-x", "2-a-x", "2-b-x", "2-tick-x", "2-c-x", "5-a-x", "5-b-x", "5-tick-x", "5-c-x" };

      assertArrayEquals(expected, serviceResults.toArray(new String[0]));
   }

}
