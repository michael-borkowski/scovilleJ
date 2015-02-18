package org.scovillej.impl.serializers;

import org.scovillej.comm.Serializer;

public abstract class BaseSerializer<T> implements Serializer<T> {

   private Class<T> clazz;

   public BaseSerializer(Class<T> clazz) {
      this.clazz = clazz;
   }

   @Override
   public Class<T> getSerializedClass() {
      return clazz;
   }
}
