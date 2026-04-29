package org.project.travelscrapper.travelconsumer;

import org.project.travelscrapper.travelconsumer.infrastructure.FlightReceiver;


public class TravelConsumerMain {
    public static void main(String[] args) {
        System.out.println("=== ARRANCANDO MÓDULO CONSUMIDOR DE VIAJES ===");
        FlightReceiver receiver = new FlightReceiver();
        receiver.startListening();
    }
}
