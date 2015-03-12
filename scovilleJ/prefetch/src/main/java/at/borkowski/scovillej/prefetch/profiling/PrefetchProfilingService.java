package at.borkowski.scovillej.prefetch.profiling;

import at.borkowski.scovillej.prefetch.Request;

public interface PrefetchProfilingService {
   void fetched(Request request, int actualSize, long tick, long duration);

   void cacheHit(Request request, long age);

   void cacheMiss(Request request);

   void lateArrival(Request request);

}
