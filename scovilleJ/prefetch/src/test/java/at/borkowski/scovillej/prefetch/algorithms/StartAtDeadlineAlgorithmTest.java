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

      Map<Request, Long> schedules = sut.schedule(req, null);

      for (Request r : req)
         assertEquals(r.getDeadline(), schedules.get(r).longValue());
   }

   @Test
   public void testOverlap() {
      Request r1, r2;
      Set<Request> req = new HashSet<>();
      req.add(r1 = new Request(1000, 100, 4));
      req.add(r2 = new Request(1000, 100, 4));

      Map<Request, Long> schedules = sut.schedule(req, null);

      assertTrue(schedules.containsKey(r1.getDeadline()));
      assertFalse(schedules.containsKey(r1.getDeadline() - 1));

      assertEquals(r1.getDeadline(), schedules.get(r1).longValue());
      assertEquals(r2.getDeadline(), schedules.get(r2).longValue());
   }
}
