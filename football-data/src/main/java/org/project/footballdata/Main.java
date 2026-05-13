package org.project.footballdata;

import org.project.footballdata.infrastructure.FootballClient;
import org.project.footballdata.core.FootballController;
import org.project.footballdata.core.FootballStore;
import org.project.footballdata.infrastructure.FootballPublisher;
import org.project.footballdata.core.FootballFeeder;


public class Main {
    public static void main(String[] args) {
        String brokerUrl = (args.length > 0) ? args[0] :
                System.getenv().getOrDefault("BROKER_URL", "tcp://localhost:61616");

        FootballFeeder feeder = new FootballClient();
        FootballStore store = new FootballPublisher(brokerUrl);
        FootballController controller = new FootballController(feeder, store);
        System.out.println("=== MÓDULO FOOTBALL-DATA INICIADO (Broker: " + brokerUrl + ") ===");

        controller.start();
    }
}