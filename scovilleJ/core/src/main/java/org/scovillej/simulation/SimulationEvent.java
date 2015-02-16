package org.scovillej.simulation;

public interface SimulationEvent {
   SimulationMember getMember();
   
   long getScheduledTick();

   void execute(SimulationContext context);
}
