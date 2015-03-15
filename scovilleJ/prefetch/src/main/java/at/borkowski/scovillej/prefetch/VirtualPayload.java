package at.borkowski.scovillej.prefetch;

public class VirtualPayload {
   private final int size;
   private final boolean transferPayload;

   public VirtualPayload(int size) {
      this(size, false);
   }

   public VirtualPayload(int size, boolean transferPayload) {
      this.size = size;
      this.transferPayload = transferPayload;
   }

   public int getSize() {
      return size;
   }

   public boolean getTransferPayload() {
      return transferPayload;
   }
}
