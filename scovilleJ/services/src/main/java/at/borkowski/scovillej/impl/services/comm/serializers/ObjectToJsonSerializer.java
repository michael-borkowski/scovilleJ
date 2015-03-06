package at.borkowski.scovillej.impl.services.comm.serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectToJsonSerializer extends BaseSerializer<Object> {

   public static String CHARSET = "UTF-8";
   private final ObjectMapper mapper;

   public ObjectToJsonSerializer() {
      super(Object.class);

      JsonFactory jf = new JsonFactory();
      mapper = new ObjectMapper(jf);
   }

   @Override
   public Object deserialize(byte[] bytes) {
      try {
         ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
         int len = bais.read();
         if (len <= 0)
            return null;
         byte[] className = new byte[len];
         while (len > 0)
            len -= bais.read(className, className.length - len, len);
         return mapper.readValue(bais, Class.forName(new String(className, CHARSET)));
      } catch (IOException | ClassNotFoundException jpEx) {
         throw new RuntimeException(jpEx);
      }
   }

   @Override
   public byte[] serialize(Object object) {
      if (object == null)
         return new byte[0];
      
      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         Class<?> clazz = object.getClass();
         byte[] className = clazz.getName().getBytes(CHARSET);
         baos.write(className.length);
         baos.write(className);
         mapper.writeValue(baos, object);
         return baos.toByteArray();
      } catch (IOException jpEx) {
         throw new RuntimeException(jpEx);
      }
   }
}
