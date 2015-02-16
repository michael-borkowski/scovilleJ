package org.scovillej;

import java.util.Collection;

public interface SimulationMember {
   Collection<SimulationEvent> generateEvents();

   String getName();
}
