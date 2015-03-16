package at.borkowski.scovillej.services.comm.impl.serializers;

import java.util.Map;

import at.borkowski.scovillej.services.comm.Serializer;

public class BuiltInSerializers {
   private BuiltInSerializers() {}

   public static void addTo(Map<Class<?>, Serializer<?>> map) {
      map.put(byte[].class, new ByteArraySerializer());
      map.put(String.class, new StringSerializer());

      map.put(Object.class, new ObjectToJsonSerializer());
   }
}
