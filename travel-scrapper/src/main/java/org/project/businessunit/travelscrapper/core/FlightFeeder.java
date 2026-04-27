package org.project.businessunit.travelscrapper.core;

import org.project.businessunit.travelscrapper.model.FlightInfo;
import java.util.List;

public interface FlightFeeder {
    List<FlightInfo> getFlights(String destinationCode);
}