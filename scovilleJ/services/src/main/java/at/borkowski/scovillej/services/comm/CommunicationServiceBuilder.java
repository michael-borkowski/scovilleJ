package at.borkowski.scovillej.services.comm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import at.borkowski.scovillej.services.comm.impl.CommunicationServiceImpl;
import at.borkowski.scovillej.simulation.ServiceProvider;
import at.borkowski.scovillej.simulation.Simulation;

/**
 * A class facilitating the creation of communication service objects (see
 * {@link CommunicationService}).
 */
public class CommunicationServiceBuilder {
   /**
    * The default buffer size used, in bytes.
    */
   public static final int DEFAULT_BUFFER_SIZE = 1024 * 1024;

   private String phase = Simulation.TICK_PHASE;
   private int bufferSize = CommunicationServiceBuilder.DEFAULT_BUFFER_SIZE;
   private final Map<String, Integer> uplink = new HashMap<>();
   private final Map<String, Integer> downlink = new HashMap<>();
   private final Map<String, Long> updelay = new HashMap<>();
   private final Map<String, Long> downdelay = new HashMap<>();
   private final List<Serializer<?>> serializers = new LinkedList<>();

   private ServiceProvider<CommunicationService> instance;

   /**
    * Sets the phase during which communiction takes place. The default is the
    * main tick phase (see {@link Simulation#TICK_PHASE}.
    * 
    * @param phase
    *           the communiction phase
    * @return this object
    */
   public CommunicationServiceBuilder communicationPhase(String phase) {
      checkUncreated();

      this.phase = phase;
      return this;
   }

   /**
    * Limits the given socket name to the given uplink and downlink byte rates.
    * 
    * @param socketName
    *           the name of the socket to limit
    * @param uplinkRate
    *           the uplink (client to server) byte rate per tick
    * @param downlinkRate
    *           the downlink (server to client) byte rate per tick
    * @return this object
    */
   public CommunicationServiceBuilder limit(String socketName, Integer uplinkRate, Integer downlinkRate) {
      checkUncreated();

      this.uplink.put(socketName, uplinkRate);
      this.downlink.put(socketName, downlinkRate);
      return this;
   }

   /**
    * Limits the given socket name to the given byte rates, both uplink and
    * downlink.
    * 
    * @param socketName
    *           the name of the socket to limit
    * @param rate
    *           the rate to set
    * @return this object
    */
   public CommunicationServiceBuilder limit(String socketName, Integer rate) {
      return limit(socketName, rate, rate);
   }

   /**
    * Adds a delay to the given socket.
    * 
    * @param socketName
    *           the name of the socket to add delay to
    * @param updelay
    *           the uplink (client to server) delay in ticks
    * @param downdelay
    *           the downlink (server to client) delay in ticks
    * @return this object
    */
   public CommunicationServiceBuilder delay(String socketName, Long updelay, Long downdelay) {
      checkUncreated();

      this.updelay.put(socketName, updelay);
      this.downdelay.put(socketName, downdelay);
      return this;
   }

   /**
    * Adds a delay to the given socket, both uplink and downlink.
    * 
    * @param socketName
    *           the name of the socket to add delay to
    * @param delay
    *           the delay in ticks to set
    * @return this object
    */
   public CommunicationServiceBuilder delay(String socketName, Long delay) {
      return delay(socketName, delay, delay);
   }

   /**
    * Sets the buffer size, in bytes, used for communictation
    * 
    * @param bufferSize
    *           the buffer size
    * @return this object
    */
   public CommunicationServiceBuilder bufferSize(int bufferSize) {
      checkUncreated();

      this.bufferSize = bufferSize;
      return this;
   }

   /**
    * Adds a custom serializer to the serializer list
    * 
    * @param serializer
    *           the serializer to add
    * @return this object
    */
   public CommunicationServiceBuilder serializer(Serializer<?> serializer) {
      checkUncreated();

      serializers.add(serializer);
      return this;
   }

   private void checkUncreated() {
      if (instance != null)
         throw new IllegalStateException("instance already created");
   }

   private void createIfNecessary() {
      if (instance != null)
         return;
      instance = new CommunicationServiceImpl(phase, uplink, downlink, updelay, downdelay, bufferSize, serializers);
   }

   /**
    * Creates the communication service.
    * 
    * @return the communication service
    */
   public ServiceProvider<CommunicationService> create() {
      createIfNecessary();
      return instance;
   }
}
