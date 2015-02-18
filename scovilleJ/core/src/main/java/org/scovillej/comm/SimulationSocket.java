package org.scovillej.comm;

import java.io.Closeable;
import java.io.IOException;

public interface SimulationSocket<T> extends Closeable {

   boolean established();

   int available() throws IOException;

   T read() throws IOException;

   void write(T object) throws IOException;

   void close();
}
