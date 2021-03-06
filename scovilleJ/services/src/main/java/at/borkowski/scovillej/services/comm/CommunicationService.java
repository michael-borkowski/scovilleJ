package at.borkowski.scovillej.services.comm;

import java.io.IOException;

/**
 * Provides socket-(name-)base communication to simulation members.
 */
public interface CommunicationService {
   /**
    * Creates a server socket listening under the given name for connections,
    * being able to transfer objects of the given class.
    * 
    * @param name
    *           the socket name
    * @param clazz
    *           the (runtime) class transferred over the socket
    * @param <T>
    *           the (static) class transferred over the socket
    * @return the server socket
    * @throws IOException
    *            if the name is already in use
    */
   <T> SimulationServerSocket<T> createServerSocket(String name, Class<T> clazz) throws IOException;

   /**
    * Creates a client socket and begins connection to the given socket name,
    * using the given object class for transfer.
    * 
    * @param name
    *           the socket name to connect to
    * @param clazz
    *           the (runtime) class transferred over the socket
    * @param <T>
    *           the (static) class transferred over the socket
    * @return the client socket
    * @throws IOException
    *            if the name is not in use or the server socket uses an
    *            incompatible data type
    */
   <T> SimulationSocket<T> beginConnect(String name, Class<T> clazz) throws IOException;

   /**
    * Sets the upink/downlink byte rates for the given socket. All present and
    * future connections with the given socket name will use these rates.
    * 
    * @param name
    *           the name of the socket to set rates
    * @param uplink
    *           the uplink rate in bytes per tick
    * @param downlink
    *           the downlink rate in bytes per tick
    */
   void setRates(String name, Integer uplink, Integer downlink);

   /**
    * Adds a data type serializer used for socket connections.
    * 
    * @param clazz
    *           the (runtime) class to serialize
    * @param serializer
    *           the serializer to use
    * @param <T>
    *           the (static) to serialize
    */
   <T> void addSerializer(Class<T> clazz, Serializer<T> serializer);

   /**
    * Returns the current uplink byte rate for the given socket.
    * 
    * @param name
    *           the socket name
    * @return the uplink byte rate, or <code>null</code> if unlimited
    */
   Integer getUplinkRate(String name);

   /**
    * Returns the current downlink byte rate for the given socket.
    * 
    * @param name
    *           the socket name
    * @return the downlink byte rate, or <code>null</code> if unlimited
    */
   Integer getDownlinkRate(String name);
}
