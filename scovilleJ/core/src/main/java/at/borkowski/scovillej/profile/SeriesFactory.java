package at.borkowski.scovillej.profile;

// TODO document this
public interface SeriesFactory<T extends Number> {
   Series<T> create();
}
