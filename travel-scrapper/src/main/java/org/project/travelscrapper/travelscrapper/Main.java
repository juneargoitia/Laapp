package org.project.travelscrapper.travelscrapper;

import org.project.travelscrapper.travelscrapper.core.FlightController;
import org.project.travelscrapper.travelscrapper.core.DestinationInfo;
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

        List<DestinationInfo> destinations = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("airports.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String code = parts[1].trim();
                    String type = (parts.length >= 3) ? parts[2].trim() : "normal";
                    destinations.add(new DestinationInfo(code, type));
                }
            }
        } catch (Exception e) {
            System.err.println("Error leyendo airports.txt: " + e.getMessage());
        }
        System.out.println("=== MÓDULO TRAVEL-SCRAPPER INICIADO (MODO INDEPENDIENTE) ===");
        controller.execute(destinations);
    }
}