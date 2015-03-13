package at.borkowski.scovillej.impl.services.comm.serializers;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import at.borkowski.scovillej.services.comm.impl.serializers.ObjectToJsonSerializer;

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
      
      TestClass parent, child;
      parent = new TestClass();
      parent.name = "parent";
      parent.number = 314;
      parent.primitive = 271;
      
      child = new TestClass();
      child.name = "child";
      child.number = 3133;
      child.primitive = 7;
      child.parent = parent;
      
      TestClass parent_, child_;
      
      parent_ = (TestClass) sut.deserialize(sut.serialize(parent));
      child_ = (TestClass) sut.deserialize(sut.serialize(child));
      
      assertEquals(parent.name, parent_.name);
      assertEquals(parent.number, parent_.number);
      assertEquals(parent.primitive, parent_.primitive);
      assertEquals(null, parent_.parent);
      
      assertEquals(child.name, child_.name);
      assertEquals(child.number, child_.number);
      assertEquals(child.primitive, child_.primitive);

      parent_ = child_.parent;

      assertEquals(parent.name, parent_.name);
      assertEquals(parent.number, parent_.number);
      assertEquals(parent.primitive, parent_.primitive);
      assertEquals(null, parent_.parent);
   }
   
}
