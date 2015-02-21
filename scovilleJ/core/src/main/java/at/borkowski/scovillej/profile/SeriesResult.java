package at.borkowski.scovillej.profile;

import java.util.Map;

/**
 * Represents the result of a series of measurements. This interface represents
 * the reader-side view of the series, ie. the interface for reading values and
 * aggregated numbers.
 *
 * @param <T>
 *           The type of number measured
 */
public interface SeriesResult<T extends Number> {

   /**
    * Returns all measured values as a map from the tick to the value.
    * 
    * @return all measured values
    */
   Map<Long, T> getAll();

   /**
    * Returns an averaged version of all values. The measured values are
    * averaged into classes with a width of <code>classWidth</code>.
    * 
    * The returned map contains all averaged results as a map from the tick (the
    * beginning, "left border" of the class) to the average value.
    * 
    * @param classWidth
    *           the width in ticks for the classes
    * @return the resulting map
    */
   Map<Long, Double> getAveraged(long classWidth);

   /**
    * Returns an average (arithmetic mean) of all measured values.
    * 
    * @return
    */
   Double getAverage();

   /**
    * Returns the median of all measured values. This method always returns a
    * {@link Double}, even if the measured values are integer and one median is
    * present. For a more exact version, see {@link #getNativeMedian()}.
    * 
    * If there are two median values (because the number of measurements is
    * even), the arithmetic mean of the two values is returned.
    * 
    * @return the median
    */
   Double getDoubleMedian();

   /**
    * Returns the standard deviation of all measured values.
    * 
    * @return the standard deviation
    */
   Double getStandardDeviation();

   /**
    * Returns the minimum value of all measured values.
    * 
    * @return the minimum value
    */
   T getMin();

   /**
    * Returns the maximum value of all measured values.
    *
    * @return the maximum value
    */
   T getMax();

   /**
    * Returns the median of all measured values. In contrast to
    * {@link #getDoubleMedian()}, this method returns an exact median if there
    * is one median. If there are two median values, the returned value depends
    * on the implemented measurement series, but will be performed in a
    * mean-like way (possible rounded towards some integer value).
    * 
    * @return
    */
   // TODO: change to an array of median values
   T getNativeMedian();

   /**
    * Determines whether the series has one median (because it has an odd number
    * of values, or because it is even and two median candidates are equal).
    * 
    * @return whether the series has one median
    */
   // TODO: change to hasSingleMedian()
   boolean hasExactMedian();

   /**
    * Returns the number of measured values.
    * 
    * @return the number of measured values
    */
   long getCount();
}
