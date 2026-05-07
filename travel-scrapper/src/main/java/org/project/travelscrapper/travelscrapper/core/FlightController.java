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
            System.out.println("   [Rate limit] Esperando 65s para cumplir políticas de API...");
            Thread.sleep(RATE_LIMIT_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void executeWithDestination(String destinationCode) {
        System.out.println(">>> Iniciando búsqueda de disponibilidad: Madrid -> " + destinationCode);
        List<FlightInfo> flights = feeder.getFlights(destinationCode);
        if (flights != null && !flights.isEmpty()) {
            store.save(flights);
            System.out.println(">>> Resultados sincronizados y enviados al servicio de viajes.");
        } else {
            System.out.println(">>> No se encontraron opciones disponibles para " + destinationCode);
        }
    }

    public void executeWithMatch(String destinationCode, LocalDate matchDate) {
        // Cálculo de fechas de la ventana de viaje
        LocalDate dateIda = matchDate.minusDays(1);
        LocalDate dateVuelta = matchDate.plusDays(1);

        System.out.println(">>> Estableciendo ventana de viaje para el evento en " + destinationCode);
        System.out.println(">>> [Salida: " + dateIda + "] | [Regreso: " + dateVuelta + "]");

        List<FlightInfo> allFlights = new ArrayList<>();

        // Gestión del trayecto de IDA
        System.out.println("--- Consultando disponibilidad de IDA (MAD -> " + destinationCode + ") ---");
        allFlights.addAll(feeder.getFlights(destinationCode, dateIda));
        esperarRateLimit();

        // Gestión del trayecto de VUELTA
        System.out.println("--- Consultando disponibilidad de REGRESO (" + destinationCode + " -> MAD) ---");
        allFlights.addAll(feeder.getFlights("MAD_RETURN_" + destinationCode, dateVuelta));
        esperarRateLimit();

        if (!allFlights.isEmpty()) {
            store.save(allFlights);
            System.out.println(">>> Plan de viaje completado. Total opciones únicas enviadas: " + allFlights.size());
        } else {
            System.out.println(">>> No se encontraron vuelos que coincidan con la ventana de viaje establecida.");
        }
    }
}