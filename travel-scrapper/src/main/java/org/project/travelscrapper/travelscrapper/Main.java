package org.project.travelscrapper.travelscrapper;

import org.project.travelscrapper.travelscrapper.core.FlightController;
import org.project.travelscrapper.travelscrapper.infrastructure.FlightPublisher;
import org.project.travelscrapper.travelscrapper.infrastructure.FlightScraper;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        FlightScraper feeder = new FlightScraper();
        FlightPublisher store = new FlightPublisher();
        FlightController controller = new FlightController(feeder, store);

        List<String> destinations = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("airports.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                destinations.add(line.split(",")[1].trim());
            }
        } catch (Exception e) {
            System.err.println("Error leyendo airports.txt");
        }
        System.out.println("=== MÓDULO TRAVEL-SCRAPPER INICIADO (MODO INDEPENDIENTE) ===");
        controller.execute(destinations);
    }
}