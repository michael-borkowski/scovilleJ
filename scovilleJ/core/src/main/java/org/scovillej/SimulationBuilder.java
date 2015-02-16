package org.scovillej;

import java.util.LinkedList;
import java.util.List;

import org.scovillej.impl.SimulationImpl;

public class SimulationBuilder {
   private final List<String> phases = new LinkedList<>();
   private final List<SimulationMember> members = new LinkedList<>();

   private Long tickCount;
   
   public SimulationBuilder() {
      phases.add(Simulation.TICK_PHASE);
   }

   public SimulationBuilder totalTicks(long tickCount) {
      this.tickCount = tickCount;
      return this;
   }
   
   public SimulationBuilder phase(String phase) {
      phases.remove(phase);
      phases.add(phase);
      return this;
   }
   
   public SimulationBuilder member(SimulationMember member) {
      members.add(member);
      return this;
   }

   public Simulation create() {
      if (tickCount == null)
         throw new IllegalStateException("tick count not set");

      List<SimulationEvent> events = new LinkedList<>();
      for (SimulationMember member : members)
         events.addAll(member.generateEvents());

      return new SimulationImpl(phases, events, tickCount);
   }
}
