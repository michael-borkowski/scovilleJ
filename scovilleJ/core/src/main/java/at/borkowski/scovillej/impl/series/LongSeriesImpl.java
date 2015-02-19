package at.borkowski.scovillej.impl.series;

import java.util.Comparator;
import java.util.TreeSet;

public class LongSeriesImpl extends NumberSeriesImpl<Long> {

   @Override
   protected TreeSet<Long> createValueTreeSet() {
      return new TreeSet<>(new Comparator<Long>() {
         public int compare(Long o1, Long o2) {
            return o1.longValue() > o2.longValue() ? 1 : -1;
         }
      });
   }

   @Override
   public Long calcNativeMedian(Long a, Long b, boolean exact) {
      return (a + b) / 2;
   }
}
