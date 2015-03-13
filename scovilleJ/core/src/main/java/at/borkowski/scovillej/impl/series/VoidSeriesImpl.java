package at.borkowski.scovillej.impl.series;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import at.borkowski.scovillej.simulation.Simulation;

/**
 * A series of measures with the only information that an event has happened,
 * but no further value.
 */
public class VoidSeriesImpl extends SeriesImpl<Void> {

   private Simulation sim;
   private final Set<Long> ticks = new HashSet<>();

   @Override
   public void measure(Void value) {
      ticks.add(sim.getCurrentTick());
   }

   @Override
   public Map<Long, Void> getAll() {
      Map<Long, Void> ret = new HashMap<>();
      for (Long tick : ticks)
         ret.put(tick, null);
      return ret;
   }

   /**
    * Returns an empty map always.
    */
   @Override
   public Map<Long, Double> getAveraged(long classWidth) {
      return new HashMap<Long, Double>();
   }

   /**
    * Returns <code>null</code> always.
    */
   @Override
   public Double getAverage() {
      return null;
   }

   /**
    * Returns <code>null</code> always.
    */
   @Override
   public Double getDoubleMedian() {
      return null;
   }

   /**
    * Returns <code>null</code> always.
    */
   @Override
   public Double getStandardDeviation() {
      return null;
   }

   /**
    * Returns <code>null</code> always.
    */
   @Override
   public Void getMin() {
      return null;
   }

   /**
    * Returns <code>null</code> always.
    */
   @Override
   public Void getMax() {
      return null;
   }

   /**
    * Returns an array with the element <code>null</code> always.
    */
   @Override
   public Void[] getNativeMedians() {
      return new Void[] { null };
   }

   /**
    * Returns <code>null</code> always.
    */
   @Override
   public Void getNativeMedian() {
      return null;
   }

   /**
    * Returns <code>null</code> always.
    */
   @Override
   public boolean hasSingleMedian() {
      return !ticks.isEmpty();
   }

   @Override
   public long getCount() {
      return ticks.size();
   }

   @Override
   public void initialize(Simulation simulation) {
      this.sim = simulation;
   }

   @Override
   public Class<Void> getValueClass() {
      return Void.class;
   }

}
