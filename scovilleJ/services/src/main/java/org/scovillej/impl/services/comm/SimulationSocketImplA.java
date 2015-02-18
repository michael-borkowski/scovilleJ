package org.scovillej.impl.services.comm;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.scovillej.services.comm.Serializer;
import org.spicej.bytes.RateLimitInputStream;
import org.spicej.ticks.TickSource;

public class SimulationSocketImplA<T> extends SimulationSocketImpl<T> {

   public SimulationSocketImplA(TickSource t, Integer uplink, Integer downlink, SimulationSocketImplB<T> clientSide, Serializer<T> serializer) throws IOException {
      PipedInputStream uplink_in = new PipedInputStream();
      PipedOutputStream uplink_out = new PipedOutputStream(uplink_in);

      PipedInputStream downlink_in = new PipedInputStream();
      PipedOutputStream downlink_out = new PipedOutputStream(downlink_in);

      setIO(rate(t, uplink, uplink_in), downlink_out, clientSide, serializer);

      clientSide.setIO(rate(t, downlink, downlink_in), uplink_out, this, serializer);
   }

   private static InputStream rate(TickSource t, Integer rate, InputStream in) {
      if (rate == null)
         return in;
      else
         return new RateLimitInputStream(in, t, rate, 1);
   }
}
