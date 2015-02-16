package org.scovillej;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.scovillej.impl.DoubleSeriesImpl;
import org.scovillej.impl.FloatSeriesImpl;
import org.scovillej.impl.IntegerSeriesImpl;
import org.scovillej.impl.LongSeriesImpl;
import org.scovillej.impl.SimulationImpl;
import org.scovillej.profile.SeriesProvider;
import org.scovillej.simulation.Simulation;
import org.scovillej.simulation.SimulationEvent;
import org.scovillej.simulation.SimulationMember;

public class SimulationBuilder {
   private final List<String> phases = new LinkedList<>();
   private final List<SimulationMember> members = new LinkedList<>();
   private final Map<String, SeriesProvider<?>> series = new HashMap<>();

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

   public SimulationBuilder seriesDouble(String symbol) {
      series.put(symbol, new DoubleSeriesImpl());
      return this;
   }

   public SimulationBuilder seriesFloat(String symbol) {
      series.put(symbol, new FloatSeriesImpl());
      return this;
   }

   public SimulationBuilder seriesLong(String symbol) {
      series.put(symbol, new LongSeriesImpl());
      return this;
   }

   public SimulationBuilder seriesInteger(String symbol) {
      series.put(symbol, new IntegerSeriesImpl());
      return this;
   }

   public Simulation create() {
      if (tickCount == null)
         throw new IllegalStateException("tick count not set");

      List<SimulationEvent> events = new LinkedList<>();
      for (SimulationMember member : members)
         events.addAll(member.generateEvents());

      return new SimulationImpl(series, phases, events, tickCount);
   }
}
