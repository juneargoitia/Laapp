package org.project.travelscrapper.core;

import org.project.travelscrapper.model.FlightInfo;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FlightController {
    private final FlightFeeder feeder;
    private final FlightStore store;

    public FlightController(FlightFeeder feeder, FlightStore store){
        this.feeder = feeder;
        this.store = store;
    }

    public void start(){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        System.out.println(">>> Sistema de captura de VUELOS iniciado.");

        scheduler.scheduleAtFixedRate(() -> {
            try {
                execute();
            } catch (Exception e) {
                System.err.println("Error en la ejecución de vuelos: " + e.getMessage());
            }
        }, 0, 1, TimeUnit.HOURS);
    }

    private void execute() {
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
