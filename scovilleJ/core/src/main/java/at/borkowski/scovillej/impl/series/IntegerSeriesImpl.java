package at.borkowski.scovillej.impl.series;

import java.util.Comparator;

/**
 * A series of {@link Integer} measures.
 */
public class IntegerSeriesImpl extends NumberSeriesImpl<Integer> {

   public IntegerSeriesImpl() {
      super(new Comparator<Integer>() {
         public int compare(Integer o1, Integer o2) {
            return o1.intValue() > o2.intValue() ? 1 : -1;
         }
      }, Integer.class);
   }

   @Override
   public Integer calcNativeMedian(Integer a, Integer b) {
      return (a + b) / 2;
   }
}
