package org.scovillej.impl.services.comm;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.scovillej.impl.services.comm.serializers.BuiltInSerializers;
import org.scovillej.services.comm.CommunicationService;
import org.scovillej.services.comm.Serializer;
import org.scovillej.services.comm.SimulationServerSocket;
import org.scovillej.services.comm.SimulationSocket;
import org.scovillej.simulation.Simulation;
import org.scovillej.simulation.SimulationContext;
import org.scovillej.simulation.SimulationEvent;
import org.scovillej.simulation.SimulationMember;
import org.spicej.impl.SimulationTickSource;
import org.spicej.ticks.TickSource;

public class CommunicationServiceImpl implements CommunicationService, SimulationMember {

   private final String phase;
   private final SimulationTickSource t = new SimulationTickSource();

   private final Map<String, SimulationServerSocketImpl<?>> serverSockets = new HashMap<>();
   private final Map<String, Class<?>> clazzes = new HashMap<>();
   private final Map<String, Integer> uplink;
   private final Map<String, Integer> downlink;
   private final Map<Class<?>, Serializer<?>> serializers = new HashMap<>();

   public CommunicationServiceImpl() {
      this(Simulation.TICK_PHASE, new HashMap<String, Integer>(), new HashMap<String, Integer>());
   }

   public CommunicationServiceImpl(String phase, Map<String, Integer> uplink, Map<String, Integer> downlink) {
      this.phase = phase;
      this.uplink = uplink;
      this.downlink = downlink;

      BuiltInSerializers.addTo(serializers);
   }

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

      SimulationServerSocketImpl<T> serverSocket = new SimulationServerSocketImpl<T>(this, name, uplink.get(name), downlink.get(name), getSerializer(clazz));
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
   public void executePhase(SimulationContext context) {
      if (context.getCurrentPhase().equals(phase))
         t.advance();
   }

   @Override
   public Collection<SimulationEvent> generateEvents() {
      return null;
   }

   public TickSource getTickSource() {
      return t;
   }

}
