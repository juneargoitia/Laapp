package org.project.view;

import org.project.control.FlightController;
import org.project.control.FlightFeeder;
import org.project.control.FlightStore;
import org.project.control.FlightScraper;
import org.project.persistence.FlightDatabase;
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