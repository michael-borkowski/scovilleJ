package at.borkowski.scovillej.impl.series;

import java.util.Comparator;

/**
 * A series of {@link Long} measures.
 */
public class LongSeriesImpl extends SeriesImpl<Long> {

   public LongSeriesImpl() {
      super(new Comparator<Long>() {
         public int compare(Long o1, Long o2) {
            return o1.longValue() > o2.longValue() ? 1 : -1;
         }
      }, Long.class);
   }

   @Override
   public Long calcNativeMedian(Long a, Long b, boolean exact) {
      return (a + b) / 2;
   }
}
