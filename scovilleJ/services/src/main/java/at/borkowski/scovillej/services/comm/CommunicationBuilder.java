package at.borkowski.scovillej.services.comm;

import java.util.HashMap;
import java.util.Map;

import at.borkowski.scovillej.impl.services.comm.CommunicationServiceImpl;
import at.borkowski.scovillej.simulation.ServiceProvider;
import at.borkowski.scovillej.simulation.Simulation;

public class CommunicationBuilder {
   public static final int DEFAULT_BUFFER_SIZE = 1024 * 1024;

   private String phase = Simulation.TICK_PHASE;
   private int bufferSize = CommunicationBuilder.DEFAULT_BUFFER_SIZE;
   private Map<String, Integer> uplink = new HashMap<>();
   private Map<String, Integer> downlink = new HashMap<>();

   private ServiceProvider<CommunicationService> instance;

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

   public CommunicationBuilder bufferSize(int bufferSize) {
      checkUncreated();

      this.bufferSize = bufferSize;
      return this;
   }

   private void checkUncreated() {
      if (instance != null)
         throw new IllegalStateException("instance already created");
   }

   private void createIfNecessary() {
      if (instance != null)
         return;
      instance = new CommunicationServiceImpl(phase, uplink, downlink, bufferSize);
   }

   public ServiceProvider<?> createProvider() {
      createIfNecessary();
      return instance;
   }
}
