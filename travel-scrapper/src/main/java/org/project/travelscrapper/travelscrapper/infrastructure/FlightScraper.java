package org.project.travelscrapper.travelscrapper.infrastructure;

import com.google.gson.*;
import org.project.travelscrapper.travelscrapper.core.FlightFeeder;
import org.project.travelscrapper.travelscrapper.model.FlightInfo;


import java.net.URI;
import java.net.http.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlightScraper implements FlightFeeder {

    private static final List<String> ALLOWED_AIRLINES = Arrays.asList("RYANAIR", "VUELING", "IBERIA", "TURKISH");

    private String getApiKey() {
        String key = System.getenv("AERODATABOX_API_KEY");
        if (key == null || key.isEmpty()) {
            throw new IllegalStateException("Variable de entorno AERODATABOX_API_KEY no definida.");
        }
        return key;
    }

    private final HttpClient http = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @Override
    public List<FlightInfo> getFlights(String destinationCode) {
        return getFlights(destinationCode, LocalDate.now());
    }

    @Override
    public List<FlightInfo> getFlights(String destinationCode, LocalDate date) {
        boolean isReturn = destinationCode.startsWith("MAD_RETURN_");
        String depIata = isReturn ? destinationCode.replace("MAD_RETURN_", "") : "MAD";
        String arrIata = isReturn ? "MAD" : destinationCode;

        List<FlightInfo> flights = new ArrayList<>();

        flights.addAll(fetchFlights(depIata, arrIata, date, "T00:00", "T11:59"));
        flights.addAll(fetchFlights(depIata, arrIata, date, "T12:00", "T23:59"));

        return flights;
    }

    private void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private List<FlightInfo> fetchFlights(String depIata, String arrIata, LocalDate date,
                                          String fromSuffix, String toSuffix) {
        sleep(5000);

        List<FlightInfo> flights = new ArrayList<>();
        String capturedAt = Instant.now().toString();

        try {
            String url = "https://aerodatabox.p.rapidapi.com/flights/airports/iata/"
                    + depIata + "/" + date + fromSuffix + "/" + date + toSuffix
                    + "?withLeg=true&direction=Departure&withCancelled=false"
                    + "&withCodeshared=true&withCargo=false&withPrivate=false&withLocation=false";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("x-rapidapi-key", getApiKey())
                    .header("x-rapidapi-host", "aerodatabox.p.rapidapi.com")
                    .GET()
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("AeroDataBox error [" + response.statusCode() + "]");
                return flights;
            }

            JsonObject root = gson.fromJson(response.body(), JsonObject.class);
            JsonArray departures = root.getAsJsonArray("departures");
            if (departures == null) return flights;

            for (JsonElement el : departures) {
                JsonObject f = el.getAsJsonObject();

                JsonObject airlineObj = f.getAsJsonObject("airline");
                String airline = (airlineObj != null && airlineObj.has("name"))
                        ? airlineObj.get("name").getAsString() : "N/A";

                if (!isAllowed(airline)) continue;

                JsonObject arrival = f.getAsJsonObject("arrival");
                if (arrival == null) continue;

                JsonObject arrAirport = arrival.getAsJsonObject("airport");
                if (arrAirport == null || !arrAirport.has("iata")) continue;

                String arrIataActual = arrAirport.get("iata").getAsString();
                if (!arrIataActual.equalsIgnoreCase(arrIata)) continue;

                String depTime = extractTime(f.getAsJsonObject("departure"));

                if (!depTime.contains(date.toString())) {
                    continue;
                }

                String flightNumber = f.has("number") ? f.get("number").getAsString().replace(" ", "") : "N/A";
                String arrTime = extractTime(arrival);
                String status = f.has("status") ? f.get("status").getAsString().toUpperCase() : "SCHEDULED";

                flights.add(new FlightInfo(flightNumber, depIata, arrIata, depTime, arrTime, status, airline, capturedAt, 0.0));
            }

            System.out.println(">>> [AeroDataBox] " + flights.size()
                    + " vuelos REALES filtrados " + depIata + " -> " + arrIata + " para el " + date);

        } catch (Exception e) {
            System.err.println("Error en fetch: " + e.getMessage());
        }

        return flights;
    }

    private boolean isAllowed(String airlineName) {
        String name = airlineName.toUpperCase();
        return ALLOWED_AIRLINES.stream().anyMatch(name::contains);
    }

    private String extractTime(JsonObject obj) {
        if (obj != null && obj.has("scheduledTime")) {
            JsonObject sched = obj.getAsJsonObject("scheduledTime");
            if (sched.has("local")) return sched.get("local").getAsString();
            else if (sched.has("utc")) return sched.get("utc").getAsString();
        }
        return "N/A";
    }
}