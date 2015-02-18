package org.scovillej.impl.comm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

import org.scovillej.comm.Serializer;
import org.scovillej.comm.SimulationSocket;

public abstract class SimulationSocketImpl<T> implements SimulationSocket<T> {

   private static final int HEADER_LENGTH = 4;

   private SimulationSocketImpl<T> otherSide;

   private Serializer<T> serializer;
   private final Queue<T> receiveQueue = new LinkedList<T>();

   private InputStream in;
   private OutputStream out;

   private byte[] nextFrame;
   private int nextFrameFilled;

   private boolean open = false, finished = false;

   @Override
   public boolean established() {
      return open;
   }

   @Override
   public int available() throws IOException {
      readRemaining();
      return receiveQueue.size();
   }

   @Override
   public T read() throws IOException {
      readRemaining();
      return receiveQueue.poll();
   }

   @Override
   public void write(T object) throws IOException {
      byte[] serialized = serializer.serialize(object);
      byte[] header = createHeader(serialized);
      out.write(header);
      out.write(serialized);
   }

   @Override
   public void close() {
      otherSide.closeInternal();
      closeInternal();
   }

   private void closeInternal() {
      open = false;
   }

   private void readRemaining() throws IOException {
      if (finished)
         throw new IOException("socket closed");

      if (in.available() == 0 && !open) {
         try {
            in.close();
         } catch (IOException ignore) {}

         try {
            out.close();
         } catch (IOException ignore) {}

         finished = true;
         return;
      }

      while (in.available() > 0) {
         if (nextFrame == null && in.available() >= HEADER_LENGTH) {
            byte[] currentHeader = new byte[HEADER_LENGTH];
            int done = 0;
            while (done < HEADER_LENGTH)
               done += in.read(currentHeader, done, HEADER_LENGTH - done);

            int nextFrameLength = ByteBuffer.wrap(currentHeader).getInt();
            nextFrame = new byte[nextFrameLength];
            nextFrameFilled = 0;
         }

         while (nextFrame != null && nextFrameFilled < nextFrame.length && in.available() > 0)
            nextFrameFilled += in.read(nextFrame, nextFrameFilled, nextFrame.length - nextFrameFilled);

         if (nextFrameFilled == nextFrame.length) {
            receiveQueue.add(serializer.deserialize(nextFrame));
            nextFrame = null;
            nextFrameFilled = 0;
         }
      }
   }

   byte[] createHeader(byte[] serialized) {
      return ByteBuffer.allocate(HEADER_LENGTH).putInt(serialized.length).array();
   }

   protected void setIO(InputStream in, OutputStream out, SimulationSocketImpl<T> otherSide, Serializer<T> serializer) {
      this.in = in;
      this.out = out;
      this.otherSide = otherSide;
      this.serializer = serializer;
      
      open = true;
   }
}
