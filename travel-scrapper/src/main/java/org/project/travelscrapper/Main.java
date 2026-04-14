package org.project.travelscrapper;

import org.project.travelscrapper.core.FlightController;
import org.project.travelscrapper.core.FlightFeeder;
import org.project.travelscrapper.core.FlightStore;
import org.project.travelscrapper.infrastructure.FlightPublisher;
import org.project.travelscrapper.infrastructure.FlightScraper;

public class Main {
    public static void main(String[] args) {
        FlightFeeder feeder = new FlightScraper();
        FlightStore store = new FlightPublisher();
        FlightController controller = new FlightController(feeder, store);
        controller.start();

    }
}