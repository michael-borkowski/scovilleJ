package at.borkowski.scovillej.services.comm.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import at.borkowski.scovillej.services.comm.CommunicationService;
import at.borkowski.scovillej.services.comm.Serializer;
import at.borkowski.scovillej.services.comm.SimulationServerSocket;
import at.borkowski.scovillej.services.comm.SimulationSocket;
import at.borkowski.scovillej.services.comm.impl.serializers.BuiltInSerializers;
import at.borkowski.scovillej.simulation.PhaseHandler;
import at.borkowski.scovillej.simulation.ServiceProvider;
import at.borkowski.scovillej.simulation.Simulation;
import at.borkowski.scovillej.simulation.SimulationContext;
import at.borkowski.scovillej.simulation.SimulationEvent;
import at.borkowski.scovillej.simulation.SimulationInitializationContext;
import at.borkowski.spicej.impl.SimulationTickSource;
import at.borkowski.spicej.ticks.TickSource;

public class CommunicationServiceImpl implements CommunicationService, ServiceProvider<CommunicationService> {

   public static final int DEFAULT_BUFFER_SIZE = 1024 * 1024;

   private final String phase;
   private final SimulationTickSource t = new SimulationTickSource();

   private final Map<String, SimulationServerSocketImpl<?>> serverSockets = new HashMap<>();
   private final Map<String, Class<?>> clazzes = new HashMap<>();
   private final Map<String, Integer> uplink, downlink;
   private final Map<String, Long> updelay, downdelay;
   private final Map<Class<?>, Serializer<?>> serializers = new HashMap<>();
   private final int bufferSize;

   public CommunicationServiceImpl() {
      this(Simulation.TICK_PHASE, new HashMap<String, Integer>(), new HashMap<String, Integer>(), new HashMap<String, Long>(), new HashMap<String, Long>(), DEFAULT_BUFFER_SIZE);
   }

   public CommunicationServiceImpl(String phase, Map<String, Integer> uplink, Map<String, Integer> downlink, Map<String, Long> updelay, Map<String, Long> downdelay, int bufferSize) {
      this.phase = phase;
      this.uplink = uplink;
      this.downlink = downlink;
      this.updelay = updelay;
      this.downdelay = downdelay;
      this.bufferSize = bufferSize;

      BuiltInSerializers.addTo(serializers);
   }

   @Override
   public void initialize(Simulation simulation, SimulationInitializationContext context) {}

   @Override
   public <T> SimulationSocket<T> beginConnect(String name, Class<T> clazz) throws IOException {
      @SuppressWarnings("unchecked")
      SimulationServerSocketImpl<T> serverSocket = (SimulationServerSocketImpl<T>) serverSockets.get(name);
      if (!serverSockets.containsKey(name))
         throw new IOException("name \"" + name + "\" not an open server socket");
      if (!clazzes.get(name).equals(clazz))
         throw new IOException("name \"" + name + "\" is of an uncompatible type (" + clazzes.get(name) + " != " + clazz + ")");
      SimulationSocketImplB<T> socket = new SimulationSocketImplB<T>();

      serverSocket.addWaiting(socket);
      return socket;
   }

   @Override
   public <T> SimulationServerSocket<T> createServerSocket(String name, Class<T> clazz) throws IOException {
      if (serverSockets.containsKey(name))
         throw new IOException("name \"" + name + "\" already in use");

      SimulationServerSocketImpl<T> serverSocket = new SimulationServerSocketImpl<T>(this, name, uplink.get(name), downlink.get(name), updelay.get(name), downdelay.get(name), getSerializer(clazz));
      serverSockets.put(name, serverSocket);
      clazzes.put(name, clazz);
      return serverSocket;
   }

   void removeServerSocket(String name) {
      serverSockets.remove(name);
   }

   @Override
   public void setRates(String name, Integer uplink, Integer downlink) {
      this.uplink.put(name, uplink);
      this.downlink.put(name, downlink);

      if (serverSockets.containsKey(name))
         serverSockets.get(name).setRates(uplink, downlink);
   }

   @Override
   public <T> void addSerializer(Class<T> clazz, Serializer<T> serializer) {
      this.serializers.put(clazz, serializer);
   }

   @SuppressWarnings("unchecked")
   private <T> Serializer<T> getSerializer(Class<T> clazz) {
      if (serializers.containsKey(clazz))
         return (Serializer<T>) serializers.get(clazz);

      for (Serializer<?> serializer : serializers.values())
         if (serializer.getSerializedClass().isAssignableFrom(clazz))
            return (Serializer<T>) serializer;

      throw new IllegalArgumentException("serializer for " + clazz + " unknown");
   }

   @Override
   public Collection<PhaseHandler> getPhaseHandlers() {
      return Arrays.asList(new PhaseHandler() {
         @Override
         public Collection<String> getPhaseSubcription() {
            return Arrays.asList(phase);
         }

         @Override
         public void executePhase(SimulationContext context) {
            t.advance();
         }
      });
   }

   @Override
   public Collection<SimulationEvent> generateEvents() {
      return null;
   }

   @Override
   public CommunicationService getService() {
      return this;
   }

   @Override
   public Class<CommunicationService> getServiceClass() {
      return CommunicationService.class;
   }

   public TickSource getTickSource() {
      return t;
   }

   public int getBufferSize() {
      return bufferSize;
   }

}
