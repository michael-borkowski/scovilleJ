package at.borkowski.scovillej.impl.series;

import java.util.Comparator;
import java.util.TreeSet;

public class DoubleSeriesImpl extends NumberSeriesImpl<Double> {

   @Override
   protected TreeSet<Double> createValueTreeSet() {
      return new TreeSet<>(new Comparator<Double>() {
         public int compare(Double o1, Double o2) {
            return o1.doubleValue() > o2.doubleValue() ? 1 : -1;
         }
      });
   }

   @Override
   protected Double calcNativeMedian(Double a, Double b, boolean exact) {
      return 0.5D * (medianA() + medianB());
   }
}
