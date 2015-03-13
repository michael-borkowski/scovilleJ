package at.borkowski.scovillej.prefetch.algorithms;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import at.borkowski.scovillej.prefetch.Request;

/**
 * This algorithm doesn't schedule any fetches.
 */
public class NullAlgorithm implements PrefetchAlgorithm {
   @Override
   public Map<Long, Request> schedule(Set<Request> requests) {
      HashMap<Long, Request> ret = new HashMap<>();
      return ret;
   }
}
