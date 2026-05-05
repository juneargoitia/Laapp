package org.project.travelscrapper.travelscrapper.core;

import com.google.gson.Gson;
import org.project.travelscrapper.travelscrapper.model.FlightInfo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FlightController {
    private final FlightFeeder feeder;
    private final FlightStore store;
    private final Gson gson = new Gson();

    private static final int RATE_LIMIT_DELAY_MS = 65_000;

    public FlightController(FlightFeeder feeder, FlightStore store) {
        this.feeder = feeder;
        this.store = store;
    }

    public void processFootballEvent(String json) {
        try {
            Map event = gson.fromJson(json, Map.class);
            Map match = (Map) event.get("match");

            String airportCode = (String) match.get("airportCode");
            String matchDateStr = (String) match.get("matchday");

            if (airportCode != null && !airportCode.equals("N/A") && !airportCode.isEmpty()) {
                System.out.println("\n[CONTROLLER] Evento recibido. Destino: "
                        + airportCode + " | Fecha: " + matchDateStr);

                if (matchDateStr != null && !matchDateStr.isEmpty()) {
                    LocalDate matchDate = LocalDate.parse(matchDateStr.substring(0, 10));
                    this.executeWithMatch(airportCode, matchDate);
                } else {
                    this.executeWithDestination(airportCode);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al procesar el JSON del evento: " + e.getMessage());
        }

    }

    private void esperarRateLimit() {
        try {
            System.out.println("   [Rate limit] Esperando 65s antes de la siguiente peticion...");
            Thread.sleep(RATE_LIMIT_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

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

    public void executeWithMatch(String destinationCode, LocalDate matchDate) {
        System.out.println("Partido en " + destinationCode + " el " + matchDate
                + ". Buscando vuelos con margen de +/- 2 dias...");

        List<FlightInfo> allFlights = new ArrayList<>();

        System.out.println("--- Vuelos de IDA (MAD -> " + destinationCode + ") ---");
        for (LocalDate date = matchDate.minusDays(2); !date.isAfter(matchDate); date = date.plusDays(1)) {
            allFlights.addAll(feeder.getFlights(destinationCode, date));
            esperarRateLimit();
        }

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