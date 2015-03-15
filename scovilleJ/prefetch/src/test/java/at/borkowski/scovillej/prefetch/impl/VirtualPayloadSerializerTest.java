package at.borkowski.scovillej.prefetch.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class VirtualPayloadSerializerTest {

   VirtualPayloadSerializer sut = new VirtualPayloadSerializer();

   @Test
   public void testSerializationWithPayload1() {
      byte[] bytes = new byte[104];
      bytes[3] = 100;
      assertArrayEquals(bytes, sut.serialize(new VirtualPayload(100, true)));
   }

   @Test
   public void testSerializationWithPayload2() {
      byte[] bytes = new byte[104];
      bytes[3] = 100;
      assertArrayEquals(bytes, sut.serialize(new VirtualPayload(100)));
   }

   @Test
   public void testSerializationWithZeroPayload() {
      byte[] bytes = new byte[4];
      assertArrayEquals(bytes, sut.serialize(new VirtualPayload(0, true)));
   }

   @Test
   public void testSerializationWithoutPayload1() {
      byte[] bytes = new byte[4];
      bytes[3] = 100;
      assertArrayEquals(bytes, sut.serialize(new VirtualPayload(100, false)));
   }

   @Test
   public void testDeserialization() {
      byte[] bytes = new byte[4];
      bytes[3] = 100;
      assertEquals(100, sut.deserialize(bytes).getSize());
      assertTrue(sut.deserialize(bytes).getTransferPayload());
   }

   @Test(expected = IllegalArgumentException.class)
   public void testDeserializationInvalid() {
      byte[] bytes = new byte[3];
      bytes[2] = 100;
      sut.deserialize(bytes);
   }

}
