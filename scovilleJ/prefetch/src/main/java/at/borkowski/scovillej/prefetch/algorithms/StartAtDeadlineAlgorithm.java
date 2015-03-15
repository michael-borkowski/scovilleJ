package at.borkowski.scovillej.prefetch.algorithms;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import at.borkowski.scovillej.prefetch.Request;
import at.borkowski.scovillej.prefetch.members.aux.RatePredictionService;

/**
 * This algorithm schedules all fetches for the deadline of their request, or
 * earlier if this slot is already taken.
 */
public class StartAtDeadlineAlgorithm implements PrefetchAlgorithm {
   @Override
   public Map<Long, Request> schedule(Collection<Request> requests, RatePredictionService ratePredictionService) {
      HashMap<Long, Request> ret = new HashMap<>();

      for (Request req : requests) {
         long start = req.getDeadline();
         while (ret.containsKey(start))
            start--;
         ret.put(start, req);
      }

      return ret;
   }
}
