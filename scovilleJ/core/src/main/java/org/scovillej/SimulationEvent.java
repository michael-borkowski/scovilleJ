package org.scovillej;

public interface SimulationEvent {
   SimulationMember getMember();
   
   long getScheduledTick();

   void execute(SimulationContext context);
}
