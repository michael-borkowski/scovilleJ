package at.borkowski.scovillej.services.comm;

import java.io.Closeable;
import java.io.IOException;

/**
 * Provides means of listening for incoming connections in a simulation.
 * 
 * @param <T>
 *           the type of data transferred
 */
public interface SimulationServerSocket<T> extends Closeable {
   /**
    * Returns the number of waiting sockets ready to be accepted (see
    * {@link #accept()}). This number is zero if there are no listening sockets
    * and at most the number of sockets which can be accepted without blocking
    * (but at least one).
    * 
    * @return an estimate of the number of waiting sockets.
    */
   int available();

   /**
    * Accepts a socket from the waiting queue. This method blocks if there are
    * not waiting sockets.
    * 
    * @return a socket from the waiting queue
    * @throws IOException
    *            if an I/O exception occurs
    */
   SimulationSocket<T> accept() throws IOException;

   /**
    * Closes the server socket and frees up resources.
    */
   // TODO test this
   void close();
}
