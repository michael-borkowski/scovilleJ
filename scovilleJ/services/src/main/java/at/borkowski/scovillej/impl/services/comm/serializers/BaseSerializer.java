package at.borkowski.scovillej.impl.services.comm.serializers;

import at.borkowski.scovillej.services.comm.Serializer;

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
