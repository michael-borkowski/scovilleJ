package org.scovillej.impl.serializers;

import java.util.Map;

import org.scovillej.comm.Serializer;

public class BuiltInSerializers {
   private BuiltInSerializers() {}

   public static void addTo(Map<Class<?>, Serializer<?>> map) {
      map.put(byte[].class, new ByteArraySerializer());
      map.put(String.class, new StringSerializer());
   }
}
