package at.borkowski.scovillej.impl.series;

import java.util.Formatter;

import at.borkowski.scovillej.profile.SeriesProvider;

public abstract class SeriesImpl<T> implements SeriesProvider<T> {

   @Override
   public String toString() {
      try (Formatter f = new Formatter()) {
         return formatMetrics(f.format("count %4d ", getCount())).toString();
      }
   }

   protected Formatter formatMetrics(Formatter f) {
      return f;
   }
}
