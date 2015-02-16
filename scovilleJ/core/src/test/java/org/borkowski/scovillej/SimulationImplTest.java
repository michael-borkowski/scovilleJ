package org.borkowski.scovillej;

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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.scovillej.Simulation;
import org.scovillej.SimulationContext;
import org.scovillej.SimulationEvent;
import org.scovillej.SimulationMember;
import org.scovillej.impl.SimulationImpl;

public class SimulationImplTest {

   Simulation sut;

   int totalTicks = 1000;
   String[] phases = { "a", "b", "tick", "c" };
   int[] yesTicks = { 0, 1, 2, 5, 10, 100, 314, 271, 420, 980, 990, 995, 998, 999 };
   int[] noTicks = { -2, -1, 1000, 1001, 1010, 1100 };

   SimulationMember member;
   Map<Integer, SimulationEvent> tick_evt = new HashMap<>();

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

      sut = new SimulationImpl(phaseNames, tick_evt.values(), totalTicks);
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

}
