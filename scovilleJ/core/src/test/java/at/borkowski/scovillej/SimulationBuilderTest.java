package at.borkowski.scovillej;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import at.borkowski.scovillej.impl.SimulationImpl;
import at.borkowski.scovillej.simulation.PhaseHandler;
import at.borkowski.scovillej.simulation.Simulation;
import at.borkowski.scovillej.simulation.SimulationContext;
import at.borkowski.scovillej.simulation.SimulationEvent;
import at.borkowski.scovillej.simulation.SimulationInitializationContext;
import at.borkowski.scovillej.simulation.SimulationMember;

public class SimulationBuilderTest {

   SimulationBuilder sut;

   @Before
   public void setUp() {
      sut = new SimulationBuilder();
   }

   @Test(expected = IllegalStateException.class)
   public void testNoTicks() {
      sut.create();
   }

   @Test
   public void testTotalTicks() {
      assertSame(sut, sut.totalTicks(10));
      assertEquals(10, sut.create().getTotalTicks());
   }

   @Test
   public void testEvents() {
      sut.member(new SimulationMember() {
         @Override
         public void initialize(Simulation simulation, SimulationInitializationContext context) {}

         @Override
         public Collection<SimulationEvent> generateEvents() {
            List<SimulationEvent> events = new LinkedList<>();
            events.add(new SimulationEvent() {

               @Override
               public long getScheduledTick() {
                  return 1;
               }

               @Override
               public Collection<String> getPhaseSubcription() {
                  return Arrays.asList(Simulation.TICK_PHASE);
               }

               @Override
               public void executePhase(SimulationContext context) {}
            });
            events.add(new SimulationEvent() {

               @Override
               public long getScheduledTick() {
                  return 3;
               }

               @Override
               public Collection<String> getPhaseSubcription() {
                  return Arrays.asList(Simulation.TICK_PHASE);
               }

               @Override
               public void executePhase(SimulationContext context) {}
            });
            return events;
         }

         @Override
         public Collection<PhaseHandler> getPhaseHandlers() {
            return null;
         }
      });
      sut.member(new SimulationMember() {
         @Override
         public void initialize(Simulation simulation, SimulationInitializationContext context) {}

         @Override
         public Collection<SimulationEvent> generateEvents() {
            List<SimulationEvent> events = new LinkedList<>();
            events.add(new SimulationEvent() {

               @Override
               public long getScheduledTick() {
                  return 5;
               }

               @Override
               public Collection<String> getPhaseSubcription() {
                  return Arrays.asList(Simulation.TICK_PHASE);
               }

               @Override
               public void executePhase(SimulationContext context) {}
            });
            events.add(new SimulationEvent() {

               @Override
               public long getScheduledTick() {
                  return 7;
               }

               @Override
               public Collection<String> getPhaseSubcription() {
                  return Arrays.asList(Simulation.TICK_PHASE);
               }

               @Override
               public void executePhase(SimulationContext context) {}
            });
            return events;
         }

         @Override
         public Collection<PhaseHandler> getPhaseHandlers() {
            return null;
         }
      });

      sut.totalTicks(10);
      SimulationImpl sim = (SimulationImpl) sut.create();
      Map<Long, List<SimulationEvent>> map = sim.test__getEventsMap();

      assertNull(map.get(0L));
      assertNotNull(map.get(1L));
      assertNull(map.get(2L));
      assertNotNull(map.get(3L));
      assertNull(map.get(4L));
      assertNotNull(map.get(5L));
      assertNull(map.get(6L));
      assertNotNull(map.get(7L));
      assertNull(map.get(8L));

      List<SimulationEvent> lst;

      lst = map.get(1L);
      assertEquals(1, lst.size());
      assertEquals(1, lst.get(0).getScheduledTick());
      lst = map.get(3L);
      assertEquals(1, lst.size());
      assertEquals(3, lst.get(0).getScheduledTick());
      lst = map.get(5L);
      assertEquals(1, lst.size());
      assertEquals(5, lst.get(0).getScheduledTick());
      lst = map.get(7L);
      assertEquals(1, lst.size());
      assertEquals(7, lst.get(0).getScheduledTick());
   }

   @Test
   public void testPhasesNoTick() {
      sut.phase("a").phase("b").phase("b").totalTicks(10);

      SimulationImpl sim = (SimulationImpl) sut.create();
      String[] phases = sim.getPhases().toArray(new String[0]);

      assertArrayEquals(new String[] { "tick", "a", "b" }, phases);
   }

   @Test
   public void testPhasesTick() {
      sut.phase("a");
      sut.phase("b");
      sut.phase("tick");
      sut.phase("c");
      sut.phase("c");

      sut.totalTicks(10);

      SimulationImpl sim = (SimulationImpl) sut.create();
      String[] phases = sim.getPhases().toArray(new String[0]);

      assertArrayEquals(new String[] { "a", "b", "tick", "c" }, phases);
   }
}
