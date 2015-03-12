package at.borkowski.scovillej.runnner;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import at.borkowski.scovillej.prefetch.PrefetchSimulationBuilder;
import at.borkowski.scovillej.prefetch.Request;
import at.borkowski.scovillej.prefetch.algorithms.IgnoreBlinkAlgorithm;
import at.borkowski.scovillej.prefetch.algorithms.NullAlgorithm;
import at.borkowski.scovillej.prefetch.algorithms.PrefetchAlgorithm;
import at.borkowski.scovillej.prefetch.algorithms.StartAtDeadlineAlgorithm;
import at.borkowski.scovillej.prefetch.profiling.PrefetchProfilingResults;
import at.borkowski.scovillej.simulation.Simulation;

public class Main {

   public static void main(String[] args) {
      List<Request> rs = new LinkedList<>();
      rs.add(new Request(100000, 5 * 1024, 22, "/data/A"));
      rs.add(new Request(200000, 1 * 1024, 22, "/data/B"));
      rs.add(new Request(300000, 503 * 1024, 22, "/data/C"));
      rs.add(new Request(400000, 3 * 1024, 22, "/data/D"));
      rs.add(new Request(500000, 25 * 1024, 22, "/data/E"));
      rs.add(new Request(600000, 20, 22, "/data/F"));
      rs.add(new Request(700000, 10 * 1024, 22, "/data/G"));
      rs.add(new Request(700001, 100 * 1024, 21, "/data/H"));
      rs.add(new Request(800000, 152 * 1024, 22, "/data/I"));
      rs.add(new Request(900000, 251 * 1024, 22, "/data/J"));

      Map<String, byte[]> files = new HashMap<>();
      for (Request r : rs)
         files.put(r.getFile(), new byte[(int) r.getData()]);

      PrefetchAlgorithm algorithm;

      algorithm = new NullAlgorithm();
      algorithm = new StartAtDeadlineAlgorithm();
      //algorithm = new IgnoreBlinkAlgorithm();

      PrefetchSimulationBuilder builder = new PrefetchSimulationBuilder().requests(rs).files(files).totalTicks(1000000).algorithm(algorithm);
      Simulation sim = builder.create();

      PrefetchProfilingResults profiling = builder.getProfiling();
      
      sim.executeToEnd();

      System.out.println("Misses (due): " + profiling.getOverdue());
      System.out.println("Hits (age):   " + profiling.getCacheHitAges());
      System.out.println();
      System.out.println("URT:          " + profiling.getURT());
      System.out.println("URT/bytes:    " + profiling.getURTperKB());

      System.out.println("End.");
   }

}
