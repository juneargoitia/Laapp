package org.project.businessunit;

import org.project.businessunit.core.Datamart;
import org.project.businessunit.infrastructure.Subscriber;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Datamart datamart = new Datamart();
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
                        System.out.println("\nPARTIDO: " + m.get("localTeam") + " vs " + m.get("visitorTeam"));
                        System.out.println("DESTINO: " + m.get("airportCode"));
                        System.out.println("VUELOS DISPONIBLES DESDE MADRID:");
                        datamart.getFlightsFor((String) m.get("airportCode")).forEach(f ->
                                System.out.println(" - " + f.get("airline") + " | Salida: " + f.get("departureTime")));
                    });
                }
            } else if (opt.equals("2")) break;
        }
    }
}