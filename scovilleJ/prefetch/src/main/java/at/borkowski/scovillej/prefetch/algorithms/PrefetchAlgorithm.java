package at.borkowski.scovillej.prefetch.algorithms;

import java.util.Map;
import java.util.Set;

import at.borkowski.scovillej.prefetch.Request;

public interface PrefetchAlgorithm {
   Map<Long, Request> schedule(Set<Request> requests);
}