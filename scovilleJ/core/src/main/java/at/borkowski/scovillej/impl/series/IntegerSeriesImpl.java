package at.borkowski.scovillej.impl.series;

import java.util.Comparator;
import java.util.TreeSet;

/**
 * A series of {@link Integer} measures.
 */
public class IntegerSeriesImpl extends NumberSeriesImpl<Integer> {

   @Override
   protected TreeSet<Integer> createValueTreeSet() {
      return new TreeSet<>(new Comparator<Integer>() {
         public int compare(Integer o1, Integer o2) {
            return o1.intValue() > o2.intValue() ? 1 : -1;
         }
      });
   }

   @Override
   public Integer calcNativeMedian(Integer a, Integer b, boolean exact) {
      return (a + b) / 2;
   }
}
