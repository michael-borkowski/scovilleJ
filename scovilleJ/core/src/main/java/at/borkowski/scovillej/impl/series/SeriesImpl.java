package at.borkowski.scovillej.impl.series;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import at.borkowski.scovillej.profile.SeriesProvider;
import at.borkowski.scovillej.simulation.Simulation;

/**
 * A base class for implementing a series of numbers.
 *
 * @param <T>
 *           The type of numbers measured
 */
public abstract class SeriesImpl<T extends Number> implements SeriesProvider<T> {
   private Simulation sim;
   private long totalTicks;

   private Map<Long, T> map;

   private TreeSet<T> values;
   private Double sum = 0D;
   private long count = 0;

   // TODO remove totalTicks
   @Override
   public void initialize(Simulation sim, long totalTicks) {
      this.sim = sim;
      this.totalTicks = totalTicks;
      map = new HashMap<>();

      // idea stolen from http://stackoverflow.com/a/14002206
      values = createValueTreeSet();
   }

   @Override
   public Map<Long, T> getAll() {
      return map;
   }

   @Override
   public Map<Long, Double> getAveraged(long classWidth) {
      long classes = (totalTicks + classWidth - 1) / classWidth;
      Map<Long, Double> result = new HashMap<>();
      for (long c = 0; c < classes; c++) {
         long start = c * classWidth;
         long end = c * classWidth + classWidth;

         List<T> values = new LinkedList<>();
         for (long tick = start; tick < end; tick++)
            if (map.get(tick) != null)
               values.add(map.get(tick));

         if (values.size() == 0)
            result.put(start, null);
         else
            result.put(start, avg(values));
      }
      return result;
   }

   /**
    * Abstract method which must be implemented by sub-classes. The method must
    * create a {@link TreeSet} of Type <code>T</code>.
    * 
    * @return a TreeSet of type T
    */
   // TODO: cange to createComparator<T>
   protected abstract TreeSet<T> createValueTreeSet();

   /**
    * Abstract method which must be implemented by sub-classes. The method must
    * calculate the median, with two values given.
    * 
    * If this method is called, it is guaranteed that there are two median
    * candidates and the implementation is reponsible of calculating the actial
    * median.
    * 
    * Note that in general, mean-like calculation should be performed.
    * 
    * @param a
    *           the first (left) median candidate
    * @param b
    *           the second (right) median candidate
    * @param exact
    *           will be removed
    * @return the median
    */
   // TODO: call only if !exact && a != b, remove exact
   protected abstract T calcNativeMedian(T a, T b, boolean exact);

   private double avg(List<T> values) {
      double sum = 0;
      for (T t : values)
         sum += t.doubleValue();
      return sum / values.size();
   }

   @Override
   public void measure(T value) {
      if (map.containsKey(sim.getCurrentTick()))
         throw new IllegalStateException("measurement for current tick already present");
      map.put(sim.getCurrentTick(), value);
      values.add(value);
      sum += value.doubleValue();
      count++;
   }

   @Override
   public Double getAverage() {
      if (count == 0)
         return null;

      return sum / count;
   }

   @Override
   public boolean hasExactMedian() {
      return count % 2 == 1;
   }

   protected T medianA() {
      if (count == 0)
         return null;

      long l = hasExactMedian() ? count / 2 : count / 2 - 1;
      Iterator<T> iter = values.iterator();
      for (int i = 0; i < l; i++)
         iter.next();
      return iter.next();
   }

   protected T medianB() {
      if (count == 0)
         return null;

      long l = count / 2;
      Iterator<T> iter = values.iterator();
      for (int i = 0; i < l; i++)
         iter.next();
      return iter.next();
   }

   @Override
   public Double getDoubleMedian() {
      if (count == 0)
         return null;

      return (medianA().doubleValue() + medianB().doubleValue()) / 2;
   }

   @Override
   public Double getStandardDeviation() {
      if (count == 0)
         return null;
      double devSum = 0, avg = getAverage();
      for (T d : values)
         devSum += Math.pow(d.doubleValue() - avg, 2);
      return Math.sqrt(devSum / count);
   }

   @Override
   public long getCount() {
      return count;
   }

   @Override
   public T getMin() {
      if (count == 0)
         return null;

      return values.first();
   }

   @Override
   public T getMax() {
      if (count == 0)
         return null;

      return values.last();
   }

   @Override
   public T getNativeMedian() {
      if (count == 0)
         return null;
      else
         return calcNativeMedian(medianA(), medianB(), hasExactMedian());
   }
}
