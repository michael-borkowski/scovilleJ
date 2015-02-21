package at.borkowski.scovillej;

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
import at.borkowski.scovillej.simulation.SimulationMember;

/**
 * A class facilitating building simulations.
 */
public class SimulationBuilder {
   private final List<String> phases = new LinkedList<>();
   private final List<SimulationMember> members = new LinkedList<>();
   private final Map<String, SeriesProvider<?>> series = new HashMap<>();
   private final Set<ServiceProvider<?>> services = new HashSet<>();

   private Long tickCount;

   /**
    * Creates a new simulation builder with all default settings this builder
    * has defaults for.
    * 
    * The default phase {@link Simulation#TICK_PHASE} is added to the phase
    * list.
    */
   public SimulationBuilder() {
      phases.add(Simulation.TICK_PHASE);
   }

   /**
    * Sets the total tick count for the simulation.
    * 
    * @param tickCount
    *           the tick count
    * @return this object
    */
   public SimulationBuilder totalTicks(long tickCount) {
      this.tickCount = tickCount;
      return this;
   }

   /**
    * Adds a new phase to the phase list. If this phase is already in the phase
    * list (for example because it is the default ("tick") phase
    * {@link Simulation#TICK_PHASE}), it is moved to the end of the list.
    * 
    * @param phase
    *           the name of the phase to add
    * @return this object
    */
   public SimulationBuilder phase(String phase) {
      phases.remove(phase);
      phases.add(phase);
      return this;
   }

   /**
    * Adds a simulation member to the simulation.
    * 
    * @param member
    *           the member to add
    * @return this object
    */
   public SimulationBuilder member(SimulationMember member) {
      members.add(member);
      return this;
   }

   /**
    * Adds a series and defines the type as {@link Double}.
    * 
    * @param symbol
    *           the name of the series to add
    * @return this object
    */
   public SimulationBuilder seriesDouble(String symbol) {
      series.put(symbol, new DoubleSeriesImpl());
      return this;
   }

   /**
    * Adds a series and defines the type as {@link Float}.
    * 
    * @param symbol
    *           the name of the series to add
    * @return this object
    */
   public SimulationBuilder seriesFloat(String symbol) {
      series.put(symbol, new FloatSeriesImpl());
      return this;
   }

   /**
    * Adds a series and defines the type as {@link Long}.
    * 
    * @param symbol
    *           the name of the series to add
    * @return this object
    */
   public SimulationBuilder seriesLong(String symbol) {
      series.put(symbol, new LongSeriesImpl());
      return this;
   }

   /**
    * Adds a series and defines the type as {@link Integer}.
    * 
    * @param symbol
    *           the name of the series to add
    * @return this object
    */
   public SimulationBuilder seriesInteger(String symbol) {
      series.put(symbol, new IntegerSeriesImpl());
      return this;
   }

   /**
    * Adds a service to this situation.
    * 
    * @param service
    *           the provider of the service
    * @return this object
    */
   public SimulationBuilder service(ServiceProvider<?> service) {
      services.add(service);
      return this;
   }

   /**
    * Creates a simulation with the defined parameters.
    * 
    * @return the simulation object
    */
   public Simulation create() {
      if (tickCount == null)
         throw new IllegalStateException("tick count not set");
      return new SimulationImpl(tickCount, phases, members, series, services);
   }
}
