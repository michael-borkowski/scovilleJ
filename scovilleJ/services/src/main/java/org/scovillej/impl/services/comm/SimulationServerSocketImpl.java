package org.scovillej.impl.services.comm;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.scovillej.services.comm.Serializer;
import org.scovillej.services.comm.SimulationServerSocket;
import org.scovillej.services.comm.SimulationSocket;

public class SimulationServerSocketImpl<T> implements SimulationServerSocket<T> {

   private final CommunicationServiceImpl owner;
   private final String name;
   private final Queue<SimulationSocketImplB<T>> waitingClientSides = new LinkedList<>();
   private final Integer uplink, downlink;
   private final Serializer<T> serializer;

   private boolean open = true;

   public SimulationServerSocketImpl(CommunicationServiceImpl owner, String name, Integer uplink, Integer downlink, Serializer<T> serializer) {
      this.owner = owner;
      this.name = name;
      this.uplink = uplink;
      this.downlink = downlink;
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

      return new SimulationSocketImplA<>(owner.getTickSource(), uplink, downlink, clientSide, serializer);
   }

   @Override
   public void close() {
      open = false;
      owner.removeServerSocket(name);
   }

}
