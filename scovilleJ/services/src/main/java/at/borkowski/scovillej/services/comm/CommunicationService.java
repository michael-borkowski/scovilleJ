package at.borkowski.scovillej.services.comm;

import java.io.IOException;

public interface CommunicationService {
   <T> SimulationServerSocket<T> createServerSocket(String name, Class<T> clazz) throws IOException;

   <T> SimulationSocket<T> beginConnect(String name, Class<T> clazz) throws IOException;

   void setRates(String name, Integer uplink, Integer downlink);

   <T> void addSerializer(Class<T> clazz, Serializer<T> serializer);
}
