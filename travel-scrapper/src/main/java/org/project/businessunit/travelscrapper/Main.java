package org.project.businessunit.travelscrapper;

import org.project.businessunit.travelscrapper.core.FlightController;
import org.project.businessunit.travelscrapper.infrastructure.FlightPublisher;
import org.project.businessunit.travelscrapper.infrastructure.FlightScraper;
import org.project.businessunit.travelscrapper.infrastructure.FootballEventListener;

public class Main {
    public static void main(String[] args) {
        FlightScraper feeder = new FlightScraper();
        FlightPublisher store = new FlightPublisher();
        FlightController controller = new FlightController(feeder, store);
        FootballEventListener footballListener = new FootballEventListener(controller);

        System.out.println("=== MÓDULO TRAVEL-SCRAPPER INICIADO ===");
        footballListener.start();
    }
}