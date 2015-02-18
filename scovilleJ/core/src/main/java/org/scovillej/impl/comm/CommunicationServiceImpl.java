package org.scovillej.impl.comm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.scovillej.comm.CommunicationService;
import org.scovillej.comm.Serializer;
import org.scovillej.comm.SimulationServerSocket;
import org.scovillej.comm.SimulationSocket;
import org.scovillej.impl.serializers.BuiltInSerializers;
import org.spicej.ticks.TickSource;

public class CommunicationServiceImpl implements CommunicationService {

   private final TickSource tickSource;

   private final Map<String, SimulationServerSocketImpl<?>> serverSockets = new HashMap<>();
   private final Map<String, Integer> uplink = new HashMap<>();
   private final Map<String, Integer> downlink = new HashMap<>();
   private final Map<Class<?>, Serializer<?>> serializers = new HashMap<>();

   public CommunicationServiceImpl(TickSource tickSource) {
      this.tickSource = tickSource;
      BuiltInSerializers.addTo(serializers);
   }

   @Override
   public <T> SimulationSocket<T> beginConnect(String name) throws IOException {
      @SuppressWarnings("unchecked")
      SimulationServerSocketImpl<T> serverSocket = (SimulationServerSocketImpl<T>) serverSockets.get(name);
      if (!serverSockets.containsKey(name))
         throw new IOException("name \"" + name + "\" not an open server socket");
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

   public TickSource getTickSource() {
      return tickSource;
   }
}
