package at.borkowski.scovillej.impl.series;

import at.borkowski.scovillej.profile.SeriesProvider;

/**
 * A base class for implementing series. Its main subclass provided by sovilleJ
 * is {@link NumberSeriesImpl}.
 * 
 * @param <T>
 *           The type of numbers measured
 */
// TODO: reomve this class?
public abstract class SeriesImpl<T extends Number> implements SeriesProvider<T> {}
