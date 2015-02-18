package org.scovillej.impl.series;

import java.util.Comparator;
import java.util.TreeSet;

public class FloatSeriesImpl extends NumberSeriesImpl<Float> {

   @Override
   protected TreeSet<Float> createValueTreeSet() {
      return new TreeSet<>(new Comparator<Float>() {
         public int compare(Float o1, Float o2) {
            return o1.floatValue() > o2.floatValue() ? 1 : -1;
         }
      });
   }

   @Override
   public Float calcNativeMedian(Float a, Float b, boolean exact) {
      return (float) (0.5D * (a + b));
   }
}
