package at.borkowski.scovillej.prefetch;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RequestTest {

   @Test
   public void test() {
      Request r = new Request(1234, 4321, 13.37);
      assertEquals(1234, r.getDeadline());
      assertEquals(4321, r.getData());
      assertEquals(13.37, r.getAvailableByterate(), 0.00001);
   }

}
