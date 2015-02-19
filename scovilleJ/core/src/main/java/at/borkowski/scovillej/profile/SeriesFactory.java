package at.borkowski.scovillej.profile;

public interface SeriesFactory<T extends Number> {
   Series<T> create();
}
