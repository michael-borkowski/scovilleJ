package at.borkowski.scovillej.impl.services.comm;

import java.io.IOException;
import java.io.InputStream;

import at.borkowski.scovillej.services.comm.Serializer;
import at.borkowski.spicej.streams.DelayedInputStream;
import at.borkowski.spicej.streams.RateLimitInputStream;
import at.borkowski.spicej.streams.util.PipedInputStream;
import at.borkowski.spicej.streams.util.PipedOutputStream;
import at.borkowski.spicej.ticks.TickSource;

public class SimulationSocketImplA<T> extends SimulationSocketImpl<T> {

   public SimulationSocketImplA(TickSource t, Integer uplink, Integer downlink, Long updelay, Long downdelay, SimulationSocketImplB<T> clientSide, Serializer<T> serializer, int bufferSize) throws IOException {
      PipedInputStream uplink_in = new PipedInputStream(bufferSize);
      PipedOutputStream uplink_out = new PipedOutputStream(uplink_in);

      PipedInputStream downlink_in = new PipedInputStream(bufferSize);
      PipedOutputStream downlink_out = new PipedOutputStream(downlink_in);

      setIO(t, delay(t, updelay, rate(t, uplink, uplink_in), bufferSize), downlink_out, clientSide, serializer);

      clientSide.setIO(t, delay(t, downdelay, rate(t, downlink, downlink_in), bufferSize), uplink_out, this, serializer);
   }

   private static InputStream delay(TickSource t, Long delay, InputStream in, int bufferSize) {
      if (delay == null)
         return in;
      
      DelayedInputStream is = new DelayedInputStream(t, in, delay, bufferSize);
      is.setNonBlocking(true);
      return is;
   }

   private static InputStream rate(TickSource t, Integer rate, InputStream in) {
      if (rate == null)
         return in;

      RateLimitInputStream is = new RateLimitInputStream(in, t, rate, 1);
      is.setNonBlocking(true);
      return is;
   }
}
