package at.borkowski.scovillej.impl.series;

import static org.junit.Assert.*;

import org.junit.Test;

public class NumberSeriesImplTest {

   @Test
   public void testKnownImplementation() {
      assertTrue(NumberSeriesImpl.getKnownSeriesClasses().contains(Integer.class));
      assertTrue(NumberSeriesImpl.getKnownSeriesClasses().contains(Long.class));
      assertTrue(NumberSeriesImpl.getKnownSeriesClasses().contains(Float.class));
      assertTrue(NumberSeriesImpl.getKnownSeriesClasses().contains(Double.class));
      assertTrue(NumberSeriesImpl.getKnownSeriesClasses().contains(Void.class));
      assertEquals(5, NumberSeriesImpl.getKnownSeriesClasses().size());

      assertTrue(NumberSeriesImpl.createIfKnown(Integer.class) instanceof IntegerSeriesImpl);
      assertTrue(NumberSeriesImpl.createIfKnown(Long.class) instanceof LongSeriesImpl);
      assertTrue(NumberSeriesImpl.createIfKnown(Float.class) instanceof FloatSeriesImpl);
      assertTrue(NumberSeriesImpl.createIfKnown(Double.class) instanceof DoubleSeriesImpl);
      assertTrue(NumberSeriesImpl.createIfKnown(Void.class) instanceof VoidSeriesImpl);
   }

}
