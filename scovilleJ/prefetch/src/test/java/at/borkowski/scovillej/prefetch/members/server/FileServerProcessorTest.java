package at.borkowski.scovillej.prefetch.members.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class FileServerProcessorTest {

   FileServerProcessor sut;
   Map<String, byte[]> files = new HashMap<>();

   @Before
   public void setUp() throws Exception {
      sut = new FileServerProcessor();

      files.put("file-a", new byte[100]);
      files.put("file-b", new byte[30]);

      sut.addFiles(files);
   }

   @Test
   public void test() {
      assertTrue(sut.hasFile("file-a"));
      assertTrue(sut.hasFile("file-b"));
      assertFalse(sut.hasFile("file-c"));
      assertEquals(100, sut.getFile("file-a").length);
      assertEquals(30, sut.getFile("file-b").length);
   }

}
