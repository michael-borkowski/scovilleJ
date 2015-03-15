package at.borkowski.scovillej.prefetch.algorithms;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import at.borkowski.scovillej.prefetch.Request;
import at.borkowski.scovillej.prefetch.members.aux.RatePredictionService;

/**
 * This algorithm doesn't schedule any fetches.
 */
public class NullAlgorithm implements PrefetchAlgorithm {
   @Override
   public Map<Long, Request> schedule(Collection<Request> requests, RatePredictionService ratePredictionService) {
      HashMap<Long, Request> ret = new HashMap<>();
      return ret;
   }
}
