package at.borkowski.scovillej.impl.services.comm.serializers;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import at.borkowski.scovillej.services.comm.impl.serializers.StringSerializer;

public class StringSerializerTest {

   public static final String CHARSET = "UTF-8";

   StringSerializer sut;

   @Before
   public void setUp() {
      sut = new StringSerializer();
   }

   @Test
   public void testSerialize() throws UnsupportedEncodingException {
      assertArrayEquals("hey there".getBytes(CHARSET), sut.serialize("hey there"));
      assertArrayEquals("hey".getBytes(CHARSET), sut.serialize("hey"));
      assertArrayEquals("".getBytes(CHARSET), sut.serialize(""));
   }

   @Test
   public void testDeserialize() throws UnsupportedEncodingException {
      assertEquals("hey there", sut.deserialize("hey there".getBytes(CHARSET)));
      assertEquals("hey", sut.deserialize("hey".getBytes(CHARSET)));
      assertEquals("", sut.deserialize("".getBytes(CHARSET)));
   }

}
