package at.borkowski.scovillej.prefetch.members.aux;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import at.borkowski.scovillej.services.comm.CommunicationService;
import at.borkowski.scovillej.simulation.PhaseHandler;
import at.borkowski.scovillej.simulation.Simulation;
import at.borkowski.scovillej.simulation.SimulationContext;
import at.borkowski.scovillej.simulation.SimulationEvent;
import at.borkowski.scovillej.simulation.SimulationInitializationContext;
import at.borkowski.scovillej.simulation.SimulationMember;

public class RateSetter implements SimulationMember {

   private List<SimulationEvent> events = new LinkedList<>();

   public RateSetter(String phase, CommunicationService communicationService, String socketName, Map<Long, Integer> limits) {
      for (Long tick : limits.keySet()) {
         Integer limit = limits.get(tick);
         events.add(new SimulationEvent() {

            @Override
            public Collection<String> getPhaseSubcription() {
               return Arrays.asList(phase);
            }

            @Override
            public void executePhase(SimulationContext context) {
               communicationService.setRates(socketName, limit, limit);
            }

            @Override
            public long getScheduledTick() {
               return tick;
            }
         });
      }
   }

   @Override
   public void initialize(Simulation simulation, SimulationInitializationContext context) {}

   @Override
   public Collection<SimulationEvent> generateEvents() {
      return events;
   }

   @Override
   public Collection<PhaseHandler> getPhaseHandlers() {
      return null;
   }

}
