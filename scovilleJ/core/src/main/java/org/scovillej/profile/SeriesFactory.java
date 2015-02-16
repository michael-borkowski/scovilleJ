package org.scovillej.profile;

public interface SeriesFactory<T extends Number> {
   Series<T> create();
}
