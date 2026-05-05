package org.project.travelscrapper.travelscrapper.core;

import org.project.travelscrapper.travelscrapper.model.FlightInfo;
import java.time.LocalDate;
import java.util.List;

public interface FlightFeeder {
    List<FlightInfo> getFlights(String destinationCode);
    List<FlightInfo> getFlights(String destinationCode, LocalDate date);
}