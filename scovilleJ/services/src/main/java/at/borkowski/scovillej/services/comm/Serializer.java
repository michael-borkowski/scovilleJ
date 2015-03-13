package at.borkowski.scovillej.services.comm;

/**
 * Represents an object capable of serializing a certain type (<code>T</code>)
 * to byte arrays, and de-serializing them again.
 *
 * @param <T>
 *           the type serialized
 */
public interface Serializer<T> {
   /**
    * Serializes an object to a byte array
    * 
    * @param object
    *           the object
    * @return the byte array
    */
   public byte[] serialize(T object);

   /**
    * De-serializes an object from a byte array
    * 
    * @param bytes
    *           the byte array
    * @return the object
    */
   public T deserialize(byte[] bytes);

   /**
    * Returns the runtime reference to the serialized type
    * 
    * @return the serialized type
    */
   public Class<T> getSerializedClass();
}
