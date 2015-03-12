package at.borkowski.scovillej.prefetch.profiling;

import at.borkowski.scovillej.profile.SeriesResult;

public interface PrefetchProfilingResults {
   SeriesResult<Long> getURT();

   SeriesResult<Long> getOverdue();

   SeriesResult<Long> getCacheHitAges();

   SeriesResult<Void> getCacheMisses();

   SeriesResult<Double> getURTperKB();

}
