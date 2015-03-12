package at.borkowski.scovillej.prefetch;

public class Request {
   private final long deadline;
   private final long data;
   private final double availableByterate;
   private final String file;

   public Request(long deadline, long data, double availableByterate, String file) {
      this.deadline = deadline;
      this.availableByterate = availableByterate;
      this.data = data;
      this.file = file;
   }

   public double getAvailableByterate() {
      return availableByterate;
   }

   public long getData() {
      return data;
   }

   public long getDeadline() {
      return deadline;
   }
   
   public String getFile() {
      return file;
   }
}
