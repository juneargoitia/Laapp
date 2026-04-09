package org.project.core;

import org.project.model.FlightInfo;
import java.util.List;

public interface FlightFeeder {
    List<FlightInfo> getFlights();
}