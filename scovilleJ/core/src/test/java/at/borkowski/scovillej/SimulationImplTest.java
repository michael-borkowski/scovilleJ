package at.borkowski.scovillej;

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

import at.borkowski.scovillej.impl.SimulationImpl;
import at.borkowski.scovillej.impl.series.DoubleSeriesImpl;
import at.borkowski.scovillej.profile.SeriesProvider;
import at.borkowski.scovillej.profile.SeriesResult;
import at.borkowski.scovillej.simulation.ServiceProvider;
import at.borkowski.scovillej.simulation.Simulation;
import at.borkowski.scovillej.simulation.SimulationContext;
import at.borkowski.scovillej.simulation.SimulationEvent;
import at.borkowski.scovillej.simulation.SimulationMember;

public class SimulationImplTest {

   Simulation sut;

   int totalTicks = 1000;
   String[] phases = { "a", "b", "tick", "c" };
   int[] yesTicks = { 0, 1, 2, 5, 10, 100, 314, 271, 420, 980, 990, 995, 998, 999 };
   int[] noTicks = { -2, -1, 1000, 1001, 1010, 1100 };

   List<String> serviceCallResults;
   List<String> serviceMemberResults;
   List<String> memberResults;

   SeriesProvider<Double> sa, sb;

   SimulationMember member;
   Map<Integer, SimulationEvent> tick_evt = new HashMap<>();

   private interface A {
      String x();
   }

   @Before
   public void setUp() {
      member = spy(new SimulationMember() {
         @Override
         public Collection<SimulationEvent> generateEvents() {
            return null;
         }

         @Override
         public void executePhase(SimulationContext context) {
            memberResults.add(context.getCurrentTick() + "-" + context.getCurrentPhase());
         }
      });

      List<SimulationMember> members = new LinkedList<>();
      members.add(member);

      List<String> phaseNames = new LinkedList<>();
      for (String phase : phases)
         phaseNames.add(phase);
      for (int tick : yesTicks)
         tick_evt.put(tick, mockEvent(tick, "c"));
      for (int tick : noTicks)
         tick_evt.put(tick, mockEvent(tick, "c"));

      Map<String, SeriesProvider<?>> series = new HashMap<>();
      series.put("series-a", sa = new DoubleSeriesImpl());
      series.put("series-b", sb = new DoubleSeriesImpl());

      Set<ServiceProvider<?>> services = new HashSet<>();
      services.add(new ServiceProvider<A>() {
         @Override
         public Collection<SimulationMember> getRequiredMembers() {
            List<SimulationMember> list = new LinkedList<>();
            list.add(new SimulationMember() {

               @Override
               public void executePhase(SimulationContext context) {
                  serviceMemberResults.add(context.getCurrentTick() + "-" + context.getCurrentPhase());
               }

               @Override
               public Collection<SimulationEvent> generateEvents() {
                  return null;
               }
            });
            return list;
         }

         @Override
         public A getService() {
            return new A() {
               @Override
               public String x() {
                  return "x";
               }
            };
         }

         @Override
         public Class<A> getServiceClass() {
            return A.class;
         }
      });

      serviceCallResults = new LinkedList<>();
      serviceMemberResults = new LinkedList<>();
      memberResults = new LinkedList<>();

      sut = new SimulationImpl(totalTicks, phaseNames, members, tick_evt.values(), series, services);
   }

   public void testTotalTicks() {
      assertEquals(totalTicks, sut.getTotalTicks());
   }

   private SimulationEvent mockEvent(final long tick, final String phase) {
      SimulationEvent event = spy(new SimulationEvent() {

         @Override
         public SimulationMember getMember() {
            return null;
         }

         @Override
         public long getScheduledTick() {
            return tick;
         }

         @Override
         public void executePhase(SimulationContext context) {
            assertNotEquals(phases.length, phase);

            assertNotNull(context);
            assertEquals(tick, context.getCurrentTick());
            assertEquals(phase, context.getCurrentPhase());

            A service = context.getService(A.class);
            if (service != null) {
               serviceCallResults.add(context.getCurrentTick() + "-" + context.getCurrentPhase() + "-" + service.x());
            }
         }

         @Override
         public String getScheduledPhase() {
            return phase;
         }

      });
      doCallRealMethod().when(event).executePhase(any(SimulationContext.class));
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
      assertFalse(sut.executedCurrentTick());

      sut.executeToEnd();

      assertEquals(totalTicks - 1, sut.getCurrentTick());
      assertTrue(sut.executedCurrentTick());

      for (int tick : yesTicks)
         verify(tick_evt.get(tick), times(1)).executePhase(any(SimulationContext.class));

      for (int tick : noTicks)
         verify(tick_evt.get(tick), never()).executePhase(any(SimulationContext.class));
   }

   @Test
   public void testFullExecution_executeStrict() {
      sut.initialize();
      assertEquals(0, sut.getCurrentTick());
      assertFalse(sut.executedCurrentTick());

      for (int i = 0; i < totalTicks; i++) {
         assertEquals(i, sut.getCurrentTick());

         sut.executeCurrentTick();

         assertEquals(i, sut.getCurrentTick());
         assertTrue(sut.executedCurrentTick());

         if (i + 1 >= totalTicks)
            break;
         sut.increaseTickStrictly();

         assertEquals(i + 1, sut.getCurrentTick());
         assertFalse(sut.executedCurrentTick());
      }
      assertEquals(totalTicks - 1, sut.getCurrentTick());
      assertTrue(sut.executedCurrentTick());

      for (int tick : yesTicks)
         verify(tick_evt.get(tick), times(1)).executePhase(any(SimulationContext.class));

      for (int tick : noTicks)
         verify(tick_evt.get(tick), never()).executePhase(any(SimulationContext.class));
   }

   @Test
   public void testPartialExecution_execute() {
      int part = 200;

      sut.initialize();
      assertEquals(0, sut.getCurrentTick());
      assertFalse(sut.executedCurrentTick());

      for (int i = 0; i < part; i++) {
         assertEquals(i, sut.getCurrentTick());

         sut.executeCurrentTick();

         assertEquals(i, sut.getCurrentTick());
         assertTrue(sut.executedCurrentTick());

         sut.increaseTick();

         assertEquals(i + 1, sut.getCurrentTick());
         assertFalse(sut.executedCurrentTick());
      }
      assertEquals(part, sut.getCurrentTick());
      assertFalse(sut.executedCurrentTick());

      for (int tick : yesTicks)
         if (tick < part)
            verify(tick_evt.get(tick), times(1)).executePhase(any(SimulationContext.class));
         else
            verify(tick_evt.get(tick), never()).executePhase(any(SimulationContext.class));

      for (int tick : noTicks)
         verify(tick_evt.get(tick), never()).executePhase(any(SimulationContext.class));
   }

   @Test
   public void testPartialExecution_executeAndIncrease() {
      int part = 200;

      sut.initialize();
      for (int i = 0; i < part; i++)
         sut.executeAndIncreaseTick();
      assertEquals(part, sut.getCurrentTick());
      assertFalse(sut.executedCurrentTick());

      for (int tick : yesTicks)
         if (tick < part)
            verify(tick_evt.get(tick), times(1)).executePhase(any(SimulationContext.class));
         else
            verify(tick_evt.get(tick), never()).executePhase(any(SimulationContext.class));

      for (int tick : noTicks)
         verify(tick_evt.get(tick), never()).executePhase(any(SimulationContext.class));
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

      String[] expected = { "0-c-x", "1-c-x", "2-c-x", "5-c-x" };

      assertArrayEquals(expected, serviceCallResults.toArray(new String[0]));
   }

   @Test
   public void testMembers() {
      sut.initialize();
      sut.executeUpToTick(6);

      List<String> expectedList = new LinkedList<>();
      for (int i = 0; i < 6; i++)
         for (String s : phases)
            expectedList.add(i + "-" + s);

      assertArrayEquals(expectedList.toArray(new String[0]), memberResults.toArray(new String[0]));
   }

   @Test
   public void testServiceMembers() {
      sut.initialize();
      sut.executeUpToTick(6);

      List<String> expectedList = new LinkedList<>();
      for (int i = 0; i < 6; i++)
         for (String s : phases)
            expectedList.add(i + "-" + s);

      assertArrayEquals(expectedList.toArray(new String[0]), serviceMemberResults.toArray(new String[0]));
   }

}
