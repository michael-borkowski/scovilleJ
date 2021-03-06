package at.borkowski.scovillej.services.comm.impl.serializers;

public class ByteArraySerializer extends BaseSerializer<byte[]> {

   public static String CHARSET = "UTF-8";

   public ByteArraySerializer() {
      super(byte[].class);
   }

   @Override
   public byte[] deserialize(byte[] bytes) {
      return bytes;
   }

   @Override
   public byte[] serialize(byte[] object) {
      return object;
   }
}
