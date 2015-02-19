package at.borkowski.scovillej.services.comm;

public interface Serializer<T> {
   public byte[] serialize(T object);

   public T deserialize(byte[] bytes);

   public Class<T> getSerializedClass();
}
