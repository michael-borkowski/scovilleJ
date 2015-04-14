package at.borkowski.scovillej.services.comm.impl;

import java.io.IOException;
import java.io.InputStream;

import at.borkowski.scovillej.services.comm.Serializer;
import at.borkowski.spicej.streams.DelayedInputStream;
import at.borkowski.spicej.streams.RateLimitInputStream;
import at.borkowski.spicej.streams.util.PipedInputStream;
import at.borkowski.spicej.streams.util.PipedOutputStream;
import at.borkowski.spicej.ticks.TickSource;

public class SimulationSocketImplA<T> extends SimulationSocketImpl<T> {

   RateLimitInputStream a_rate, b_rate;
   DelayedInputStream a_delay, b_delay;

   public SimulationSocketImplA(TickSource t, Integer uplink, Integer downlink, Long updelay, Long downdelay, SimulationSocketImplB<T> clientSide, Serializer<T> serializer, int bufferSize) throws IOException {
      PipedInputStream uplink_in = new PipedInputStream(bufferSize);
      PipedOutputStream uplink_out = new PipedOutputStream(uplink_in);

      PipedInputStream downlink_in = new PipedInputStream(bufferSize);
      PipedOutputStream downlink_out = new PipedOutputStream(downlink_in);
      
      uplink_in.setExceptionOnDeadlock(true);
      downlink_in.setExceptionOnDeadlock(true);
      
      setIO(t, a_delay = delay(t, updelay, a_rate = rate(t, uplink, uplink_in), bufferSize), downlink_out, clientSide, serializer);

      clientSide.setIO(t, b_delay = delay(t, downdelay, b_rate = rate(t, downlink, downlink_in), bufferSize), uplink_out, this, serializer);
   }

   private static DelayedInputStream delay(TickSource t, Long delay, InputStream in, int bufferSize) {
      if (delay == null)
         delay = 0L;

      DelayedInputStream is = new DelayedInputStream(t, in, delay, bufferSize);
      is.setNonBlocking(true);
      return is;
   }

   private static RateLimitInputStream rate(TickSource t, Integer rate, InputStream in) {
      if (rate == null)
         rate = Integer.MAX_VALUE;

      RateLimitInputStream is = new RateLimitInputStream(in, t, rate, 1);
      is.setNonBlocking(true);
      return is;
   }

   public void setRates(Integer uplink, Integer downlink) {
      if (uplink == null)
         uplink = Integer.MAX_VALUE;
      if (downlink == null)
         downlink = Integer.MAX_VALUE;

      a_rate.setByteRate(uplink);
      b_rate.setByteRate(downlink);
   }
}
