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

   /**
    * Formats metrics for this class using the supplied formatter. Sub-classes
    * should override this method to provide detailed string representations of
    * the metrics (min, max, average, etc.)
    * 
    * @param formatter
    *           the formatter to use
    * @return the formatter itself (the supplied parameter
    *         <code>formatter</code>)
    */
   protected Formatter formatMetrics(Formatter formatter) {
      return formatter;
   }
}
