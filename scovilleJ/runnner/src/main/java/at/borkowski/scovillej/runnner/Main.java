package at.borkowski.scovillej.runnner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
      InputStream configurationSource;
      if (args.length > 1) {
         usage();
         return;
      } else if (args.length == 0 || "-".equals(args[0])) {
         configurationSource = System.in;
      } else {
         try {
            configurationSource = new FileInputStream(args[0]);
         } catch (FileNotFoundException e) {
            e.printStackTrace();
            usage();
            return;
         }
      }

      if (configurationSource != System.in) {
         try {
            configurationSource.close();
         } catch (IOException e) {
            e.printStackTrace();
            return;
         }
      }

      List<Request> rs = new LinkedList<>();
      rs.add(new Request(100000, 5 * 1024, 22));
      rs.add(new Request(200000, 1 * 1024, 22));
      rs.add(new Request(300000, 503 * 1024, 22));
      rs.add(new Request(400000, 3 * 1024, 22));
      rs.add(new Request(500000, 25 * 1024, 22));
      rs.add(new Request(600000, 20, 22));
      rs.add(new Request(700000, 10 * 1024, 22));
      rs.add(new Request(700001, 100 * 1024, 21));
      rs.add(new Request(800000, 152 * 1024, 22));
      rs.add(new Request(900000, 251 * 1024, 22));

      PrefetchAlgorithm algorithm;

      algorithm = new NullAlgorithm();
      algorithm = new StartAtDeadlineAlgorithm();
      algorithm = new IgnoreBlinkAlgorithm();

      Map<Long, Integer> limits = new HashMap<>();

      limits.put(0L, 80);
      limits.put(510000L, 22);

      PrefetchSimulationBuilder builder = new PrefetchSimulationBuilder().requests(rs).totalTicks(1000000).algorithm(algorithm).limits(limits);
      Simulation sim = builder.create();
      PrefetchProfilingResults profiling = builder.getProfiling();

      sim.executeToEnd();

      System.out.println();
      System.out.println();
      System.out.println("Misses (due): " + profiling.getOverdue());
      System.out.println("Hits (age):   " + profiling.getCacheHitAges());
      System.out.println("URT:          " + profiling.getURT());
      System.out.println("URT/bytes:    " + profiling.getURTperKB());

      System.out.println("End.");
   }

   private static void usage() {
      System.err.println("Usage: runner            reads configuration from standard input");
      System.err.println("       runner -          reads configuration from standard input");
      System.err.println("       runner <filename> reads configuration from filename");
   }

}
