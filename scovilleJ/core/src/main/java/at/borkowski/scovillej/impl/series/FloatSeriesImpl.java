package at.borkowski.scovillej.impl.series;

import java.util.Comparator;

/**
 * A series of {@link Float} measures.
 */
public class FloatSeriesImpl extends NumberSeriesImpl<Float> {

   public FloatSeriesImpl() {
      super(new Comparator<Float>() {
         public int compare(Float o1, Float o2) {
            return o1.floatValue() > o2.floatValue() ? 1 : -1;
         }
      }, Float.class);
   }

   @Override
   public Float calcNativeMedian(Float a, Float b) {
      return (float) (0.5D * (a + b));
   }
}
