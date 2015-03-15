package at.borkowski.scovillej.prefetch;

/**
 * This class represent a request which is known to be made by client code.
 * 
 * A request consists of a deadline, a predicted amount of data, a predicted
 * byterate with which the data source will provide the data (this is not the
 * link bandwidth, which might be significantly smaller) and the name of the
 * file which will be requested.
 */
public class Request {
   private final long deadline;
   private final int data;
   private final double availableByterate;
   private final String file;

   /**
    * Creates a request object
    * 
    * @param deadline
    *           the deadline
    * @param data
    *           the amount of data
    * @param availableByterate
    *           the provided byte rate
    * @param file
    *           the file name
    */
   public Request(long deadline, int data, double availableByterate, String file) {
      this.deadline = deadline;
      this.availableByterate = availableByterate;
      this.data = data;
      this.file = file;
   }

   /**
    * Returns the available byte rate
    * 
    * @return the available byte rate
    */
   public double getAvailableByterate() {
      return availableByterate;
   }

   /**
    * Returns the predicted data size
    * 
    * @return the predicted data size
    */
   public int getData() {
      return data;
   }

   /**
    * Returns the deadline until which the request must be satisfied
    * 
    * @return the deadline
    */
   public long getDeadline() {
      return deadline;
   }

   /**
    * Returns the requested file name
    * 
    * @return the file name
    */
   public String getFile() {
      return file;
   }
}
