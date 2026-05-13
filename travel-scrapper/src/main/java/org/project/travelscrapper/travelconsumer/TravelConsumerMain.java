package org.project.travelscrapper.travelconsumer;

import org.project.travelscrapper.travelconsumer.infrastructure.FlightReceiver;


public class TravelConsumerMain {
    public static void main(String[] args) {
        String brokerUrl = (args.length > 0) ? args[0] :
                System.getenv().getOrDefault("BROKER_URL", "tcp://localhost:61616");

        System.out.println("=== ARRANCANDO MÓDULO CONSUMIDOR DE VIAJES ===");

        FlightReceiver receiver = new FlightReceiver(brokerUrl);
        receiver.startListening();
    }
}
