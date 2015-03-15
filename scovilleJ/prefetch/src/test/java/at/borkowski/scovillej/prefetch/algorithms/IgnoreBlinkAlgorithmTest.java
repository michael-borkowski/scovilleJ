package at.borkowski.scovillej.prefetch.algorithms;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import at.borkowski.scovillej.prefetch.Request;

public class IgnoreBlinkAlgorithmTest {

   IgnoreRatePredictionAlgorithm sut;

   @Before
   public void setUp() throws Exception {
      sut = new IgnoreRatePredictionAlgorithm();
   }

   @Test
   public void test() {
      Set<Request> req = new HashSet<>();
      req.add(new Request(1000, 100, 4));
      req.add(new Request(2000, 300, 4));
      req.add(new Request(3000, 10, 5));
      req.add(new Request(3010, 300, 5));

      Map<Long, Request> schedules = sut.schedule(req, null);
      Map<Request, Long> times = new HashMap<>();

      for (Entry<Long, Request> entry : schedules.entrySet())
         times.put(entry.getValue(), entry.getKey());

      for (Request r : req)
         assertTrue(times.get(r) <= r.getDeadline() - (r.getData() / r.getAvailableByterate()));
   }

   @Test
   public void testOverlap() {
      Set<Request> req = new HashSet<>();
      req.add(new Request(1000, 100, 4));
      req.add(new Request(1000, 100, 4));

      Map<Long, Request> schedules = sut.schedule(req, null);
      Map<Request, Long> times = new HashMap<>();

      for (Entry<Long, Request> entry : schedules.entrySet())
         times.put(entry.getValue(), entry.getKey());

      for (Request r : req)
         assertTrue(times.get(r) <= r.getDeadline() - (r.getData() / r.getAvailableByterate()));
   }
}
