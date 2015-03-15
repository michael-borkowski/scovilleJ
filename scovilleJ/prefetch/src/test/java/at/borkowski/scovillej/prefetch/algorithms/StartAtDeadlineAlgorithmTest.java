package at.borkowski.scovillej.prefetch.algorithms;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import at.borkowski.scovillej.prefetch.Request;

public class StartAtDeadlineAlgorithmTest {

   StartAtDeadlineAlgorithm sut;

   @Before
   public void setUp() throws Exception {
      sut = new StartAtDeadlineAlgorithm();
   }

   @Test
   public void test() {
      Set<Request> req = new HashSet<>();
      req.add(new Request(1000, 100, 4));
      req.add(new Request(2000, 300, 4));
      req.add(new Request(3000, 10, 5));
      req.add(new Request(3010, 300, 5));

      Map<Long, Request> schedules = sut.schedule(req);
      Map<Request, Long> times = new HashMap<>();

      for (Entry<Long, Request> entry : schedules.entrySet())
         times.put(entry.getValue(), entry.getKey());

      for (Request r : req)
         assertEquals(r.getDeadline(), times.get(r).longValue());
   }

   @Test
   public void testOverlap() {
      Request r1, r2;
      Set<Request> req = new HashSet<>();
      req.add(r1 = new Request(1000, 100, 4));
      req.add(r2 = new Request(1000, 100, 4));

      Map<Long, Request> schedules = sut.schedule(req);
      Map<Request, Long> times = new HashMap<>();

      for (Entry<Long, Request> entry : schedules.entrySet())
         times.put(entry.getValue(), entry.getKey());

      assertTrue(schedules.containsKey(r1.getDeadline()));
      assertTrue(schedules.containsKey(r1.getDeadline() - 1));

      if (schedules.get(r1.getDeadline()) == r1)
         assertSame(r2, schedules.get(r1.getDeadline() - 1));
      else if (schedules.get(r1.getDeadline()) == r2)
         assertSame(r1, schedules.get(r1.getDeadline() - 1));
      else
         fail();
   }
}
