package at.borkowski.scovillej.prefetch.algorithms;

import java.util.Map;
import java.util.Set;

import at.borkowski.scovillej.prefetch.Request;
import at.borkowski.scovillej.prefetch.members.aux.RatePredictionService;

/**
 * This interface represents an algorithm for scheduling prefetches for
 * requests.
 */
public interface PrefetchAlgorithm {
   Map<Long, Request> schedule(Set<Request> requests, RatePredictionService ratePredictionService);
}
