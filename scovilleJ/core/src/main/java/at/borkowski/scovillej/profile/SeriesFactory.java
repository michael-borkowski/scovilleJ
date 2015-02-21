package at.borkowski.scovillej.profile;

/**
 * Represents a factory of {@link Series} objects.
 * 
 * @param <T>
 *           The type for the created series.
 */
public interface SeriesFactory<T extends Number> {
   /**
    * Creates a series object of the given type <code>T</code>.
    * 
    * @return the series object
    */
   Series<T> create();
}
