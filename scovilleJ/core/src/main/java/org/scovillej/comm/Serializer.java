package org.scovillej.comm;

public interface Serializer<T> {
   public byte[] serialize(T object);

   public T deserialize(byte[] bytes);

   public Class<T> getSerializedClass();
}
