package at.borkowski.scovillej.profile;

import at.borkowski.scovillej.simulation.Simulation;

/**
 * Represents a series provider, which is responsible for both writing (
 * {@link Series}) and reading ({@link SeriesResult}) values.
 *
 * @param <T>
 *           The type of numbers measured
 */
public interface SeriesProvider<T> extends Series<T>, SeriesResult<T> {
   /**
    * Initializes the series provider. This method is guaranteed to be called
    * before any methods of {@link Series} or {@link SeriesResult}.
    * 
    * @param simulation
    *           The simulation this series is to be performed at
    */
   void initialize(Simulation simulation);

   /**
    * Returns the runtime class type for <code>T</code>
    * 
    * @return the class object for <code>T</code>
    */
   Class<T> getValueClass();
}
