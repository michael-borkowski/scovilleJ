package at.borkowski.scovillej.services.comm.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

import at.borkowski.scovillej.services.comm.Serializer;
import at.borkowski.scovillej.services.comm.SimulationSocket;
import at.borkowski.spicej.ticks.TickListener;
import at.borkowski.spicej.ticks.TickSource;

public abstract class SimulationSocketImpl<T> implements SimulationSocket<T> {
   private static final int HEADER_LENGTH = 4;

   private SimulationSocketImpl<T> otherSide;

   private Serializer<T> serializer;
   private final Queue<T> receiveQueue = new LinkedList<T>();

   private InputStream in;
   private OutputStream out;

   private boolean blockIsHeader = true;
   private byte[] nextBlock = new byte[HEADER_LENGTH];
   private int nextBlockFilled = 0;

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

      while (nextBlock != null && nextBlockFilled < nextBlock.length && in.available() > 0) {
         nextBlockFilled += in.read(nextBlock, nextBlockFilled, nextBlock.length - nextBlockFilled);

         if (nextBlock != null && nextBlockFilled == nextBlock.length) {
            if (blockIsHeader) {
               nextBlock = new byte[ByteBuffer.wrap(nextBlock).getInt()];
            } else {
               receiveQueue.add(serializer.deserialize(nextBlock));
               nextBlock = new byte[HEADER_LENGTH];
            }
            nextBlockFilled = 0;
            blockIsHeader = !blockIsHeader;
         }
      }
   }

   byte[] createHeader(byte[] serialized) {
      return ByteBuffer.allocate(HEADER_LENGTH).putInt(serialized.length).array();
   }

   protected void setIO(TickSource t, InputStream in, OutputStream out, SimulationSocketImpl<T> otherSide, Serializer<T> serializer) {
      this.in = in;
      this.out = out;
      this.otherSide = otherSide;
      this.serializer = serializer;

      open = true;

      t.addListener(new AvailableUpdater());
   }

   private class AvailableUpdater implements TickListener {

      @Override
      public void tick(long tick) {
         try {
            if (open && !finished)
               readRemaining();
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }

   }

}
