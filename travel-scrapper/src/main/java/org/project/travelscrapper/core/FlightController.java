package org.project.travelscrapper.core;

import org.project.travelscrapper.model.FlightInfo;
import java.util.List;

public class FlightController {
    private final FlightFeeder feeder;
    private final FlightStore store;

    public FlightController(FlightFeeder feeder, FlightStore store){
        this.feeder = feeder;
        this.store = store;
    }

    public void execute() {
        System.out.println(("Iniciando Scraping de vuelos..."));
        List<FlightInfo> flights = feeder.getFlights();
        if (flights != null && !flights.isEmpty()) {
            store.save(flights);
            System.out.println(("Se han procesado " + flights.size() + " vuelos."));
        } else {
            System.out.println(("No se han encontrado vuelos en esta ejecución."));
        }
    }
}
