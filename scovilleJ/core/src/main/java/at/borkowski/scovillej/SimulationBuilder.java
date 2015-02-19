package at.borkowski.scovillej;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import at.borkowski.scovillej.impl.SimulationImpl;
import at.borkowski.scovillej.impl.series.DoubleSeriesImpl;
import at.borkowski.scovillej.impl.series.FloatSeriesImpl;
import at.borkowski.scovillej.impl.series.IntegerSeriesImpl;
import at.borkowski.scovillej.impl.series.LongSeriesImpl;
import at.borkowski.scovillej.profile.SeriesProvider;
import at.borkowski.scovillej.simulation.ServiceProvider;
import at.borkowski.scovillej.simulation.Simulation;
import at.borkowski.scovillej.simulation.SimulationEvent;
import at.borkowski.scovillej.simulation.SimulationMember;

public class SimulationBuilder {
   private final List<String> phases = new LinkedList<>();
   private final List<SimulationMember> members = new LinkedList<>();
   private final Map<String, SeriesProvider<?>> series = new HashMap<>();
   private final Set<ServiceProvider<?>> services = new HashSet<>();

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

   public SimulationBuilder service(ServiceProvider<?> service) {
      services.add(service);
      return this;
   }

   public Simulation create() {
      if (tickCount == null)
         throw new IllegalStateException("tick count not set");

      Collection<SimulationEvent> tmp;

      List<SimulationEvent> events = new LinkedList<>();
      for (SimulationMember member : members)
         if ((tmp = member.generateEvents()) != null)
            events.addAll(tmp);

      return new SimulationImpl(tickCount, phases, members, events, series, services);
   }
}
