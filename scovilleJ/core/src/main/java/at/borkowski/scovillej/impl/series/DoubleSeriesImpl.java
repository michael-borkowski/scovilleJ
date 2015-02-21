package at.borkowski.scovillej.impl.series;

import java.util.Comparator;

/**
 * A series of {@link Double} measures.
 */
public class DoubleSeriesImpl extends SeriesImpl<Double> {
   public DoubleSeriesImpl() {
      super(new Comparator<Double>() {
         public int compare(Double o1, Double o2) {
            return o1.doubleValue() > o2.doubleValue() ? 1 : -1;
         }
      }, Double.class);
   }

   @Override
   protected Double calcNativeMedian(Double a, Double b) {
      return 0.5D * (a + b);
   }
}
