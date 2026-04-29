package org.project.businessunit;

import org.project.businessunit.core.Datamart;
import org.project.businessunit.infrastructure.Subscriber;
import org.project.businessunit.infrastructure.RecordLoader;
import java.util.Scanner;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Datamart datamart = new Datamart();
        RecordLoader loader = new RecordLoader(datamart);
        loader.loadHistoricalData();
        Subscriber rtSubscriber = new Subscriber(datamart);
        rtSubscriber.start();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- CHAMPIONS FLIGHT OPTIMIZER ---");
            System.out.println("1. Listar Partidos y Ofertas");
            System.out.println("2. Salir");
            System.out.print("Seleccione una opción: ");

            String opt = scanner.nextLine();
            if (opt.equals("1")) {
                if (datamart.getMatches().isEmpty()) {
                    System.out.println("Aún no hay partidos recibidos...");
                } else {
                    datamart.getMatches().values().forEach(m -> {
                        String local = String.valueOf(m.get("localTeam"));
                        String visitor = String.valueOf(m.get("visitorTeam"));
                        String code = String.valueOf(m.get("airportCode")).toUpperCase();

                        System.out.println("\n========================================");
                        System.out.println("PARTIDO: " + local + " vs " + visitor);
                        System.out.println("DESTINO: " + code);
                        System.out.println("----------------------------------------");

                        List<Map<String, Object>> flights = datamart.getFlightsFor(code);
                        if (flights.isEmpty()) {
                            System.out.println(" > No se han encontrado vuelos para este destino.");
                        } else {
                            System.out.println("VUELOS DISPONIBLES DESDE MADRID:");
                            flights.forEach(f ->
                                    System.out.println(" - " + f.get("airline") + " | Salida: " + f.get("departureTime") + " | Estado: " + f.get("flightStatus")));
                        }
                    });
                }
            } else if (opt.equals("2")) {
                System.out.println("Cerrando sistema...");
                System.exit(0);
            }
        }
    }
}