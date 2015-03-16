package at.borkowski.scovillej.services.comm;

import java.io.Closeable;
import java.io.IOException;

/**
 * Represents a socket providing communication with a socket on the side of
 * another member.
 * 
 * @param <T>
 *           the type of data transferred
 */
public interface SimulationSocket<T> extends Closeable {

   /**
    * Returns true if a connection is established with the other socket (ie. if
    * the server socket has accepted the connection).
    * 
    * @return whether the socket connection is established
    */
   boolean established();

   /**
    * Returns the amount of objects ready for receiving. This method returns
    * zero if no objects can be received without blocking, and at most the
    * number of object which can be received without blocking (but at least
    * one).
    * 
    * @return an estimate of objects ready for receiving
    * @throws IOException
    *            if any I/O operation fails
    */
   int available() throws IOException;

   /**
    * Receives an object from this socket. This method blocks if no objects are
    * available.
    * 
    * @return the received object
    * @throws IOException
    *            if receiving fails
    */
   T read() throws IOException;

   /**
    * Writes an object to this socket.
    * 
    * @param object
    *           the object to be written
    * @throws IOException
    *            if sending fails
    */
   void write(T object) throws IOException;

   /**
    * Closes this socket.
    */
   void close();
}
