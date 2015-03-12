package at.borkowski.scovillej.simulation;

import java.util.List;

import at.borkowski.scovillej.profile.SeriesResult;

/**
 * Represents a simulation.
 * 
 * A simulation is a series of events, which have a notion of virtual time
 * called <b>ticks</b>. Ticks are separated into <b>phases</b>. Each tick is
 * processed by processing each of its phases. The order of phases is consistent
 * throughout the simulation.
 * 
 * The total number of ticks is pre-defined for a simulation (see
 * {@link #getTotalTicks()}).
 * 
 * A simulation has a collection of <b>members</b>, represented by
 * {@link SimulationMember} objects, which are called for each phase in each
 * tick. Members may interact by using <b>service</b> objects, however, the
 * services must make sure that no communication is allowed during one phase.
 * 
 * Furthermore, a simulation provides its members with a possibility of
 * recording values for profiling, called <b>series</b>. The series results can
 * later be obtained via {@link #getSeries(String, Class)}.
 *
 */
public interface Simulation {
   /**
    * The name of the main phase, the <b>tick</b> phase. Each simulation is
    * guaranteed to have this phase. It is used to denote the "default" phase,
    * ie. the phase where the first-class-citizen members are supposed to
    * perform their action.
    * 
    * Members providing services and auxiliary members are encouraged to use
    * their own phases.
    */
   public static final String TICK_PHASE = "tick";

   /**
    * Executes the simulation from the current tick until the total number of
    * ticks defined (see {@link #getTotalTicks()}).
    */
   void executeToEnd();

   /**
    * Executes the current tick. This method may only be called if the
    * simulation has not executed the current tick yet. Otherwise, an
    * {@link IllegalStateException} is thrown.
    */
   void executeCurrentTick();

   /**
    * Executes the current tick and advanced to the next tick. This method may
    * only be called if the simulation has not executed the current tick yet.
    * Otherwise, an {@link IllegalStateException} is thrown.
    */
   void executeAndIncreaseTick();

   /**
    * Returns the current tick. This tick is either ready for processing (
    * {@link #executedCurrentTick()} is false) or has already been processed (
    * {@link #executedCurrentTick()} is true).
    * 
    * @return the current tick
    */
   long getCurrentTick();

   /**
    * Advances to the next tick. This method may only be called if the
    * simulation has already executed the current tick.
    * 
    * If this method is called and the simulation has finished processing the
    * last tick, no action is taken. See {@link #increaseTickStrictly()} for a
    * version where this behavior is different.
    */
   void increaseTick();

   /**
    * Advances to the next tick. This method may only be called if the
    * simulation has already executed the current tick.
    * 
    * If this method is called and the simulation has finished processing the
    * last tick, {@link IllegalStateException} is thrown. See
    * {@link #increaseTickStrictly()} for a version where this behavior is
    * different.
    */
   void increaseTickStrictly();

   /**
    * Determines whether the current tick has been prepared (return value is
    * <code>false</code>) or has already been processed (returned value is
    * <code>true</code>).
    * 
    * @return whether the current tick has already been processed
    */
   boolean executedCurrentTick();

   /**
    * Executes the simulation up to the given tick, stopping at this tick in a
    * non-processed state ({@link #executedCurrentTick()} is false).
    * 
    * @param tick
    *           the tick to stop before
    */
   void executeUpToTick(long tick);

   /**
    * Returns the number of ticks this simulation has been configured to
    * process.
    * 
    * @return the total number of ticks for this simulation
    */
   long getTotalTicks();

   /**
    * Returns the series result for a given series. Note that while this method
    * may be called mid-simulation and will return correct results for the
    * current tick, advancing by ticks may render the returned object unusable
    * or have it provide invalid results. Generally, the returned object is
    * value only until the next advancing in the simulation.
    * 
    * If the symbol is not known of the class does not match the originally
    * created series, <code>null</code> is returned.
    * 
    * @param symbol
    *           the symbol to return the series result for
    * @param clazz
    *           the class of the series
    * @param <T>
    *           the type of series to reutrn
    * @return the series result
    */
   <T> SeriesResult<T> getSeries(String symbol, Class<T> clazz);

   /**
    * Returns the phases used in this simulation.
    * 
    * @return the phases used in this simulation.
    */
   List<String> getPhases();
}
