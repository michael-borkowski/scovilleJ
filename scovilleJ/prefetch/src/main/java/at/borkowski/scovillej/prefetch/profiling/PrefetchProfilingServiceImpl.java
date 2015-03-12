package at.borkowski.scovillej.prefetch.profiling;

import java.util.Arrays;
import java.util.Collection;

import at.borkowski.scovillej.prefetch.Request;
import at.borkowski.scovillej.profile.Series;
import at.borkowski.scovillej.profile.SeriesResult;
import at.borkowski.scovillej.simulation.PhaseHandler;
import at.borkowski.scovillej.simulation.ServiceProvider;
import at.borkowski.scovillej.simulation.Simulation;
import at.borkowski.scovillej.simulation.SimulationContext;
import at.borkowski.scovillej.simulation.SimulationEvent;
import at.borkowski.scovillej.simulation.SimulationInitializationContext;

public class PrefetchProfilingServiceImpl implements PrefetchProfilingService, PrefetchProfilingResults, ServiceProvider<PrefetchProfilingService> {

   private static String URT = "prefetch-profiling-urt";
   private static String OVERDUE = "prefetch-profiling-overdue";
   private static String AGE = "prefetch-profiling-age";
   private static String MISS = "prefetch-profiling-miss";
   private static String URTperKB = "prefetch-profiling-urt-per-byte";

   private final String phase;
   private Simulation simulation;

   private long currentTick;
   private Series<Long> seriesURT;
   private Series<Long> seriesOverdue;
   private Series<Long> seriesAge;
   private Series<Void> seriesMisses;
   private Series<Double> seriesURTperKB;

   public PrefetchProfilingServiceImpl(String phase) {
      this.phase = phase;
   }

   @Override
   public void initialize(Simulation simulation, SimulationInitializationContext context) {
      this.simulation = simulation;
      seriesURT = context.getSeries(URT, Long.class);
      seriesOverdue = context.getSeries(OVERDUE, Long.class);
      seriesAge = context.getSeries(AGE, Long.class);
      seriesMisses = context.getSeries(MISS, Void.class);
      seriesURTperKB = context.getSeries(URTperKB, Double.class);
   }

   @Override
   public Collection<PhaseHandler> getPhaseHandlers() {
      return Arrays.asList(new PhaseHandler() {
         @Override
         public void executePhase(SimulationContext context) {
            currentTick = context.getCurrentTick();
         }

         @Override
         public Collection<String> getPhaseSubcription() {
            return Arrays.asList(phase);
         }
      });
   }

   @Override
   public Collection<SimulationEvent> generateEvents() {
      return null;
   }

   @Override
   public PrefetchProfilingService getService() {
      return this;
   }

   @Override
   public Class<PrefetchProfilingService> getServiceClass() {
      return PrefetchProfilingService.class;
   }

   @Override
   public void fetched(Request request, int actualSize, long tick, long duration) {
      long overdue = currentTick - request.getDeadline();
      System.out.printf("%d - fetched %s (overdue %d) (%d B in %d t, %.2f B/t\n", tick, request.getFile(), overdue, actualSize, duration, (double) actualSize / duration);
      overdue = Math.max(0, overdue);
      seriesURT.measure(overdue);
      seriesURTperKB.measure((double) 1000 * overdue / request.getData());
   }

   @Override
   public void cacheHit(Request request, long age) {
      System.out.printf("%d - cache hit for %s (age %d)\n", currentTick, request.getFile(), age);
      seriesAge.measure(age);
   }

   @Override
   public void cacheMiss(Request request) {
      System.out.printf("%d - cache miss for %s\n", currentTick, request.getFile());
      seriesMisses.measure(null);
   }

   @Override
   public void lateArrival(Request request) {
      long overdue = currentTick - request.getDeadline();
      System.out.printf("%d - late arrival for %s (overdue %d)\n", currentTick, request.getFile(), overdue);
      seriesOverdue.measure(overdue);
   }

   @Override
   public SeriesResult<Long> getCacheHitAges() {
      return simulation.getSeries(AGE, Long.class);
   }

   @Override
   public SeriesResult<Void> getCacheMisses() {
      return simulation.getSeries(MISS, Void.class);
   }

   @Override
   public SeriesResult<Long> getOverdue() {
      return simulation.getSeries(OVERDUE, Long.class);
   }

   @Override
   public SeriesResult<Long> getURT() {
      return simulation.getSeries(URT, Long.class);
   }

   @Override
   public SeriesResult<Double> getURTperKB() {
      return simulation.getSeries(URTperKB, Double.class);
   }
}