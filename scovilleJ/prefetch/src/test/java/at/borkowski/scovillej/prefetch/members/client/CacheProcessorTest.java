package at.borkowski.scovillej.prefetch.members.client;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CacheProcessorTest {

   CacheProcessor sut;

   @Before
   public void setUp() throws Exception {
      sut = new CacheProcessor();
   }

   @Test
   public void test() {
      assertFalse(sut.hasFile("file"));

      sut.save("file", null, 123);

      assertTrue(sut.hasFile("file"));
      assertEquals(123, sut.getTimestamp("file"));
   }

}
