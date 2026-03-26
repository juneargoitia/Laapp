package org.project.control;

import org.project.model.FlightInfo;
import java.util.List;

public interface FlightFeeder {
    List<FlightInfo> getFlights();
}