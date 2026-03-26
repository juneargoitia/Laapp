package org.project.view;

import org.project.control.FlightController;
import org.project.control.FlightFeeder;
import org.project.control.FlightStore;
import org.project.control.FlightScraper;
import org.project.persistence.FlightDatabase;

public class Main {
    public static void main(String[] args) {
        FlightFeeder feeder = new FlightScraper();
        FlightStore store = new FlightDatabase();

        FlightController controller = new FlightController(feeder, store);
        controller.execute();

        System.out.println("El módulo de Vuelos está listo.");
    }
}