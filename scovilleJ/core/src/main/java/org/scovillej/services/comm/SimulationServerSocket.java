package org.scovillej.services.comm;

import java.io.Closeable;
import java.io.IOException;

public interface SimulationServerSocket<T> extends Closeable {
   int available();

   SimulationSocket<T> accept() throws IOException;
   
   void close();
}
