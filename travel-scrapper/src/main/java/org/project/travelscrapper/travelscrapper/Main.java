package org.project.travelscrapper.travelscrapper;

import org.project.travelscrapper.travelscrapper.core.FlightController;
import org.project.travelscrapper.travelscrapper.infrastructure.FlightPublisher;
import org.project.travelscrapper.travelscrapper.infrastructure.FlightScraper;
import org.project.travelscrapper.travelscrapper.infrastructure.FootballEventListener;

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