package org.project.travelscrapper.travelscrapper.core;

import org.project.travelscrapper.travelscrapper.model.FlightInfo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class FlightController {
    private final FlightFeeder feeder;
    private final FlightStore store;

    // Plan gratuito de AviationStack: 1 peticion cada 60 segundos
    private static final int RATE_LIMIT_DELAY_MS = 65_000;

    public FlightController(FlightFeeder feeder, FlightStore store) {
        this.feeder = feeder;
        this.store = store;
    }

    private void esperarRateLimit() {
        try {
            System.out.println("   [Rate limit] Esperando 65s antes de la siguiente peticion...");
            Thread.sleep(RATE_LIMIT_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Metodo original: busca solo para hoy sin margen de fechas
    public void executeWithDestination(String destinationCode) {
        System.out.println("Iniciando busqueda de vuelos Madrid -> " + destinationCode);

        List<FlightInfo> flights = feeder.getFlights(destinationCode);

        if (flights != null && !flights.isEmpty()) {
            store.save(flights);
            System.out.println("Vuelos procesados y enviados al Topic Travel.");
        } else {
            System.out.println("No se encontraron vuelos para " + destinationCode);
        }
    }

    // Metodo con fecha del partido: busca ida (matchDate-2 hasta matchDate)
    // y vuelta (matchDate hasta matchDate+2)
    public void executeWithMatch(String destinationCode, LocalDate matchDate) {
        System.out.println("Partido en " + destinationCode + " el " + matchDate
                + ". Buscando vuelos con margen de +/- 2 dias...");

        List<FlightInfo> allFlights = new ArrayList<>();

        // Vuelos de IDA: desde 2 dias antes hasta el dia del partido
        System.out.println("--- Vuelos de IDA (MAD -> " + destinationCode + ") ---");
        for (LocalDate date = matchDate.minusDays(2); !date.isAfter(matchDate); date = date.plusDays(1)) {
            allFlights.addAll(feeder.getFlights(destinationCode, date));
            esperarRateLimit();
        }

        // Vuelos de VUELTA: desde el dia del partido hasta 2 dias despues
        System.out.println("--- Vuelos de VUELTA (" + destinationCode + " -> MAD) ---");
        for (LocalDate date = matchDate; !date.isAfter(matchDate.plusDays(2)); date = date.plusDays(1)) {
            allFlights.addAll(feeder.getFlights("MAD_RETURN_" + destinationCode, date));
            esperarRateLimit();
        }

        if (!allFlights.isEmpty()) {
            store.save(allFlights);
            System.out.println("Total vuelos encontrados y enviados: " + allFlights.size());
        } else {
            System.out.println("No se encontraron vuelos para " + destinationCode
                    + " en el rango de fechas del partido.");
        }
    }
}