package at.borkowski.scovillej.services.comm.impl;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import at.borkowski.scovillej.services.comm.Serializer;
import at.borkowski.scovillej.services.comm.SimulationServerSocket;
import at.borkowski.scovillej.services.comm.SimulationSocket;

public class SimulationServerSocketImpl<T> implements SimulationServerSocket<T> {

   private final CommunicationServiceImpl owner;
   private final String name;
   private final Queue<SimulationSocketImplB<T>> waitingClientSides = new LinkedList<>();
   private final Integer uplink, downlink;
   private final Long updelay, downdelay;
   private final Serializer<T> serializer;

   private final List<SimulationSocketImplA<T>> children = new LinkedList<>();

   private boolean open = true;

   public SimulationServerSocketImpl(CommunicationServiceImpl owner, String name, Integer uplink, Integer downlink, Long updelay, Long downdelay, Serializer<T> serializer) {
      this.owner = owner;
      this.name = name;
      this.uplink = uplink;
      this.downlink = downlink;
      this.updelay = updelay;
      this.downdelay = downdelay;
      this.serializer = serializer;
   }

   void addWaiting(SimulationSocketImplB<T> clientSide) {
      if (!open)
         throw new IllegalStateException("server socket closed");
      this.waitingClientSides.add(clientSide);
   }

   @Override
   public int available() {
      return waitingClientSides.size();
   }

   @Override
   public SimulationSocket<T> accept() throws IOException {
      SimulationSocketImplB<T> clientSide = waitingClientSides.poll();
      if (clientSide == null)
         return null;

      SimulationSocketImplA<T> child = new SimulationSocketImplA<>(owner.getTickSource(), uplink, downlink, updelay, downdelay, clientSide, serializer, owner.getBufferSize());
      children.add(child);
      return child;
   }

   @Override
   public void close() {
      open = false;
      owner.removeServerSocket(name);
   }

   public void setRates(Integer uplink, Integer downlink) {
      for (SimulationSocketImplA<T> child : children)
         child.setRates(uplink, downlink);
   }

}
