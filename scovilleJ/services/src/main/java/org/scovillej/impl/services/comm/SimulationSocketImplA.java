package org.scovillej.impl.services.comm;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.scovillej.services.comm.Serializer;

import at.borkowski.spicej.bytes.RateLimitInputStream;
import at.borkowski.spicej.ticks.TickSource;

public class SimulationSocketImplA<T> extends SimulationSocketImpl<T> {

   public SimulationSocketImplA(TickSource t, Integer uplink, Integer downlink, SimulationSocketImplB<T> clientSide, Serializer<T> serializer, int bufferSize) throws IOException {
      PipedInputStream uplink_in = new PipedInputStream(bufferSize);
      PipedOutputStream uplink_out = new PipedOutputStream(uplink_in);

      PipedInputStream downlink_in = new PipedInputStream(bufferSize);
      PipedOutputStream downlink_out = new PipedOutputStream(downlink_in);

      setIO(t, rate(t, uplink, uplink_in), downlink_out, clientSide, serializer);

      clientSide.setIO(t, rate(t, downlink, downlink_in), uplink_out, this, serializer);
   }

   private static InputStream rate(TickSource t, Integer rate, InputStream in) {
      if (rate == null)
         return in;

      RateLimitInputStream is = new RateLimitInputStream(in, t, rate, 1);
      is.setNonBlocking(true);
      return is;
   }
}
