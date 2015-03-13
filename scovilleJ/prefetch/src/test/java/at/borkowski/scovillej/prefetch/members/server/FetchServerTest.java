package at.borkowski.scovillej.prefetch.members.server;

import org.junit.Before;
import org.junit.Test;

public class FetchServerTest {

   FetchServer sut;
   
   @Before
   public void setUp() throws Exception {
      sut = new FetchServer("socket");
   }

   @Test
   public void test() {
   }

}
