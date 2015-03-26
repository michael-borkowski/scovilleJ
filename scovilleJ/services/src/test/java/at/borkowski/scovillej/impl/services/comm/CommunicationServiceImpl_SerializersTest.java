package at.borkowski.scovillej.impl.services.comm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import at.borkowski.scovillej.services.comm.Serializer;
import at.borkowski.scovillej.services.comm.SimulationServerSocket;
import at.borkowski.scovillej.services.comm.SimulationSocket;
import at.borkowski.scovillej.services.comm.impl.CommunicationServiceImpl;

public class CommunicationServiceImpl_SerializersTest {

   CommunicationServiceImpl sut;

   @Before
   public void setUp() {
      sut = new CommunicationServiceImpl();
   }

   @Test
   public void testExotic() throws IOException {
      sut.setRates("accepter", null, null);
      sut.addSerializer(ExoticElement.class, new ExoticSerializer());

      SimulationServerSocket<ExoticElement> serverSocket = sut.createServerSocket("accepter", ExoticElement.class);
      assertEquals(0, serverSocket.available());
      assertNull(serverSocket.accept());

      SimulationSocket<ExoticElement> socketA = sut.beginConnect("accepter", ExoticElement.class);
      assertFalse(socketA.established());
      assertEquals(1, serverSocket.available());

      SimulationSocket<ExoticElement> socketB = serverSocket.accept();
      assertTrue(socketA.established());
      assertTrue(socketB.established());

      assertEquals(0, socketA.available());
      assertEquals(0, socketB.available());

      ////////////////////////////////////////

      socketA.write(new ExoticElement("hey there!"));

      assertEquals(0, socketA.available());
      assertEquals(1, socketB.available());

      assertEquals("hey there!", socketB.read().element);

      assertEquals(0, socketA.available());
      assertEquals(0, socketB.available());
      
      socketA.close();
      socketB.close();
      serverSocket.close();
   }

   @Test
   public void testSpecificExotic() throws IOException {
      sut.setRates("accepter", null, null);
      sut.addSerializer(ExoticElement.class, new ExoticSerializer());

      SimulationServerSocket<ExoticElement> serverSocket = sut.createServerSocket("accepter", ExoticElement.class);
      assertEquals(0, serverSocket.available());
      assertNull(serverSocket.accept());

      SimulationSocket<ExoticElement> socketA = sut.beginConnect("accepter", ExoticElement.class);
      assertFalse(socketA.established());
      assertEquals(1, serverSocket.available());

      SimulationSocket<ExoticElement> socketB = serverSocket.accept();
      assertTrue(socketA.established());
      assertTrue(socketB.established());

      assertEquals(0, socketA.available());
      assertEquals(0, socketB.available());

      ////////////////////////////////////////

      socketA.write(new SpecificExoticElement("hey there!"));

      assertEquals(0, socketA.available());
      assertEquals(1, socketB.available());

      assertEquals("hey there!", socketB.read().element);

      assertEquals(0, socketA.available());
      assertEquals(0, socketB.available());
   }

   private class ExoticElement {
      public String element;

      public ExoticElement(String element) {
         this.element = element;
      }
   }

   private class SpecificExoticElement extends ExoticElement {
      public SpecificExoticElement(String string) {
         super(string);
      }
   }

   private class ExoticSerializer implements Serializer<ExoticElement> {
      private static final String PREFIX = "exotic:";

      public ExoticElement deserialize(byte[] bytes) {
         try {
            String string = new String(bytes, "UTF8");
            if (!string.startsWith(PREFIX))
               throw new RuntimeException();
            return new ExoticElement(string.substring(PREFIX.length()));
         } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
         }
      }

      @Override
      public Class<ExoticElement> getSerializedClass() {
         return ExoticElement.class;
      }

      @Override
      public byte[] serialize(ExoticElement object) {
         try {
            return (PREFIX + object.element).getBytes("UTF8");
         } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
         }
      }
   }
}
