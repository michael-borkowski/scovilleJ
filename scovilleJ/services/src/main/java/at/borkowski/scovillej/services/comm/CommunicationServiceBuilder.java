package at.borkowski.scovillej.services.comm;

import java.util.HashMap;
import java.util.Map;

import at.borkowski.scovillej.impl.services.comm.CommunicationServiceImpl;
import at.borkowski.scovillej.simulation.ServiceProvider;
import at.borkowski.scovillej.simulation.Simulation;

public class CommunicationServiceBuilder {
   public static final int DEFAULT_BUFFER_SIZE = 1024 * 1024;

   private String phase = Simulation.TICK_PHASE;
   private int bufferSize = CommunicationServiceBuilder.DEFAULT_BUFFER_SIZE;
   private final Map<String, Integer> uplink = new HashMap<>();
   private final Map<String, Integer> downlink = new HashMap<>();
   private final Map<String, Long> updelay = new HashMap<>();
   private final Map<String, Long> downdelay = new HashMap<>();

   private ServiceProvider<CommunicationService> instance;

   public CommunicationServiceBuilder communicationPhase(String phase) {
      checkUncreated();

      this.phase = phase;
      return this;
   }

   public CommunicationServiceBuilder limit(String socketName, Integer uplinkRate, Integer downlinkRate) {
      checkUncreated();

      this.uplink.put(socketName, uplinkRate);
      this.downlink.put(socketName, downlinkRate);
      return this;
   }

   public CommunicationServiceBuilder limit(String socketName, Integer rate) {
      return limit(socketName, rate, rate);
   }

   public CommunicationServiceBuilder delay(String socketName, Long delay) {
      return delay(socketName, delay, delay);
   }

   public CommunicationServiceBuilder delay(String socketName, Long updelay, Long downdelay) {
      checkUncreated();

      this.updelay.put(socketName, updelay);
      this.downdelay.put(socketName, downdelay);
      return this;
   }

   public CommunicationServiceBuilder bufferSize(int bufferSize) {
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
      instance = new CommunicationServiceImpl(phase, uplink, downlink, updelay, downdelay, bufferSize);
   }

   public ServiceProvider<?> create() {
      createIfNecessary();
      return instance;
   }
}
