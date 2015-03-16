package at.borkowski.scovillej.profile;

/**
 * Represents a measurement series. This interface provides the writer-side view
 * of the series, ie. the possibility for a simulation member to add measured
 * values.
 *
 * @param <T>
 *           The type of number measured
 */
public interface Series<T> {

   /**
    * Adds a measured value to the series. The value is added using the current
    * tick.
    * 
    * @param value
    *           the value to add
    */
   void measure(T value);
}
