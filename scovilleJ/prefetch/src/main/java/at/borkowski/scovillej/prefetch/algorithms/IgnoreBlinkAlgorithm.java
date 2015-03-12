package at.borkowski.scovillej.prefetch.algorithms;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import at.borkowski.scovillej.prefetch.Request;

public class IgnoreBlinkAlgorithm implements PrefetchAlgorithm {
   public final static long CONNECTION_OVERHEAD = 5;
   public final static double ALPHA = 1;

   @Override
   public Map<Long, Request> schedule(Set<Request> requests) {
      HashMap<Long, Request> ret = new HashMap<>();

      List<Request> sortedByDeadline = new LinkedList<Request>(requests);
      Collections.sort(sortedByDeadline, new Comparator<Request>() {
         @Override
         public int compare(Request o1, Request o2) {
            return o1.getDeadline() > o2.getDeadline() ? -1 : 1;
         }
      });

      long previousStart = Long.MAX_VALUE;

      for (Request req : sortedByDeadline) {
         long start = getStart(previousStart, req);
         while (ret.containsKey(start))
            start--;
         previousStart = start;
         ret.put(start, req);
      }

      return ret;
   }

   private long getStart(long busyUntil, Request req) {
      long required = (long) (req.getData() / req.getAvailableByterate()) + 1;
      required += CONNECTION_OVERHEAD;
      required /= ALPHA;
      return Math.min(busyUntil, req.getDeadline()) - required;
   }
}
