package at.borkowski.scovillej.prefetch.algorithms;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import at.borkowski.scovillej.prefetch.Request;

public class StartAtDeadlineAlgorithm implements PrefetchAlgorithm {
   @Override
   public Map<Long, Request> schedule(Set<Request> requests) {
      HashMap<Long, Request> ret = new HashMap<>();

      for (Request req : requests)
         ret.put(req.getDeadline(), req);

      return ret;
   }
}
