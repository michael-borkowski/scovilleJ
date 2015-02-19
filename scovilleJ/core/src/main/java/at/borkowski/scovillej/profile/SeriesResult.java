package at.borkowski.scovillej.profile;

import java.util.Map;

//TODO document this
public interface SeriesResult<T extends Number> {
   Map<Long, T> getAll();

   Map<Long, Double> getAveraged(long classWidth);
   
   Double getAverage();
   
   Double getDoubleMedian();
   
   Double getStandardDeviation();
   
   T getMin();
   
   T getMax();

   T getNativeMedian();

   boolean hasExactMedian();
   
   long getCount();
}
