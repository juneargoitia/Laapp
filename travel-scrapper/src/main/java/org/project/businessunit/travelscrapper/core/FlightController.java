package org.project.businessunit.travelscrapper.core;

import org.project.businessunit.travelscrapper.model.FlightInfo;
import java.util.List;


public class FlightController {
    private final FlightFeeder feeder;
    private final FlightStore store;

    public FlightController(FlightFeeder feeder, FlightStore store) {
        this.feeder = feeder;
        this.store = store;
    }

    public void executeWithDestination(String destinationCode) {
        System.out.println("Iniciando Scraping de vuelos Madrid -> " + destinationCode);

        List<FlightInfo> flights = feeder.getFlights(destinationCode);

        if (flights != null && !flights.isEmpty()) {
            store.save(flights);
            System.out.println("Vuelos procesados y enviados al Topic Travel.");
        } else {
            System.out.println("No se encontraron vuelos para " + destinationCode);
        }
    }
}