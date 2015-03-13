package at.borkowski.scovillej.impl.services.comm.serializers;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import at.borkowski.scovillej.services.comm.impl.serializers.ByteArraySerializer;

public class ByteArraySerializerTest {

   ByteArraySerializer sut;

   @Before
   public void setUp() {
      sut = new ByteArraySerializer();
   }

   @Test
   public void testSerialize() {
      assertArrayEquals(new byte[] { 1, 3, 3, 7, 42, 31, 4 }, sut.serialize(new byte[] { 1, 3, 3, 7, 42, 31, 4 }));
      assertArrayEquals(new byte[] { 1 }, sut.serialize(new byte[] { 1 }));
      assertArrayEquals(new byte[] {}, sut.serialize(new byte[] {}));
   }

   @Test
   public void testDeserialize() {
      assertArrayEquals(new byte[] { 1, 3, 3, 7, 42, 31, 4 }, sut.deserialize(new byte[] { 1, 3, 3, 7, 42, 31, 4 }));
      assertArrayEquals(new byte[] { 1 }, sut.deserialize(new byte[] { 1 }));
      assertArrayEquals(new byte[] {}, sut.deserialize(new byte[] {}));
   }

}
