package org.project;

import org.project.core.FlightController;
import org.project.core.FlightFeeder;
import org.project.core.FlightStore;
import org.project.infrastructure.FlightScraper;
import org.project.infrastructure.FlightDatabase;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        FlightFeeder feeder = new FlightScraper();
        FlightStore store = new FlightDatabase();
        FlightController controller = new FlightController(feeder, store);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        System.out.println(">>> Sistema de captura de VUELOS iniciado.");

        scheduler.scheduleAtFixedRate(() -> {
            try {
                controller.execute();
            } catch (Exception e) {
                System.err.println("Error en la ejecución de vuelos: " + e.getMessage());
            }
        }, 0, 1, TimeUnit.HOURS);
    }
}