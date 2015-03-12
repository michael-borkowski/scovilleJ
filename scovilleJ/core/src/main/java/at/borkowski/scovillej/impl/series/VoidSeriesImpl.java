package at.borkowski.scovillej.impl.series;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import at.borkowski.scovillej.simulation.Simulation;

/**
 * A series of {@link Integer} measures.
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

   @Override
   public Map<Long, Double> getAveraged(long classWidth) {
      return new HashMap<Long, Double>();
   }

   @Override
   public Double getAverage() {
      return null;
   }

   @Override
   public Double getDoubleMedian() {
      return null;
   }

   @Override
   public Double getStandardDeviation() {
      return null;
   }

   @Override
   public Void getMin() {
      return null;
   }

   @Override
   public Void getMax() {
      return null;
   }

   @Override
   public Void[] getNativeMedians() {
      return new Void[] { null };
   }

   @Override
   public Void getNativeMedian() {
      return null;
   }

   @Override
   public boolean hasSingleMedian() {
      return true;
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
