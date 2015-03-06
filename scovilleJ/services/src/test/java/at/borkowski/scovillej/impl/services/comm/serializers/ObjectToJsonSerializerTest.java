package at.borkowski.scovillej.impl.services.comm.serializers;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

public class ObjectToJsonSerializerTest {

   public static final String CHARSET = "UTF-8";

   ObjectToJsonSerializer sut;

   @Before
   public void setUp() {
      sut = new ObjectToJsonSerializer();
   }

   @Test
   public void testSerialize() throws UnsupportedEncodingException {
      assertEquals("heyho string", sut.deserialize(sut.serialize("heyho string")));
      assertEquals(null, sut.deserialize(sut.serialize(null)));
      assertEquals(3141, sut.deserialize(sut.serialize(new Integer(3141))));
      assertEquals(3141L, sut.deserialize(sut.serialize(new Long(3141))));
      assertArrayEquals(new byte[] { 3, 1, 4, 1 }, (byte[]) sut.deserialize(sut.serialize(new byte[] { 3, 1, 4, 1 })));
   }

}
