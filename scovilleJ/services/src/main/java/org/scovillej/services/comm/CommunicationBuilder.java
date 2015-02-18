package org.scovillej.services.comm;

import java.util.HashMap;
import java.util.Map;

import org.scovillej.impl.services.comm.CommunicationServiceImpl;
import org.scovillej.simulation.Simulation;
import org.scovillej.simulation.SimulationMember;

public class CommunicationBuilder {
   private String phase = Simulation.TICK_PHASE;
   private Map<String, Integer> uplink = new HashMap<>();
   private Map<String, Integer> downlink = new HashMap<>();

   private CommunicationServiceImpl instance;

   public CommunicationBuilder communicationPhase(String phase) {
      checkUncreated();
      
      this.phase = phase;
      return this;
   }

   public CommunicationBuilder limit(String socketName, Integer uplinkRate, Integer downlinkRate) {
      checkUncreated();
      
      this.uplink.put(socketName, uplinkRate);
      this.downlink.put(socketName, downlinkRate);
      return this;
   }

   public CommunicationBuilder limit(String socketName, Integer rate) {
      return limit(socketName, rate, rate);
   }

   private void checkUncreated() {
      if (instance != null)
         throw new IllegalStateException("instance already created");
   }

   private void createIfNecessary() {
      if (instance != null)
         return;
      instance = new CommunicationServiceImpl(phase, uplink, downlink);
   }

   public SimulationMember createMember() {
      createIfNecessary();
      return instance;
   }
}
