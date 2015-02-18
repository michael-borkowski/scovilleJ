package org.scovillej.impl.serializers;

import java.io.UnsupportedEncodingException;

public class StringSerializer extends BaseSerializer<String> {

   public static String CHARSET = "UTF-8";

   public StringSerializer() {
      super(String.class);
   }

   @Override
   public String deserialize(byte[] bytes) {
      try {
         return new String(bytes, CHARSET);
      } catch (UnsupportedEncodingException e) {
         throw utf8wtf(e);
      }
   }

   @Override
   public byte[] serialize(String object) {
      try {
         return object.getBytes(CHARSET);
      } catch (UnsupportedEncodingException e) {
         throw utf8wtf(e);
      }
   }

   private Error utf8wtf(UnsupportedEncodingException e) {
      return new Error("utf-8 unknown", e);
   }
}
