package org.scovillej.services.comm;

import org.junit.Test;
import org.scovillej.SimulationBuilder;
import org.scovillej.impl.services.comm.CommunicationServiceImpl;
import org.scovillej.simulation.Simulation;

public class CommIntegrationTest {
   @Test
   public void test() {
      SimulationBuilder builder = new SimulationBuilder();
      builder.totalTicks(1000);
      builder.member(new CommunicationServiceImpl("comm"));
      builder.phase(Simulation.TICK_PHASE);
      builder.phase("comm");
      
      Simulation simulation = builder.create();
      simulation.initialize();

      simulation.executeToEnd();
   }
}
