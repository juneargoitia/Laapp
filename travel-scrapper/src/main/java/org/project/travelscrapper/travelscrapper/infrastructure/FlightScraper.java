package org.project.travelscrapper.travelscrapper.infrastructure;

import com.google.gson.*;
import org.project.travelscrapper.travelscrapper.core.FlightFeeder;
import org.project.travelscrapper.travelscrapper.model.FlightInfo;

import java.net.URI;
import java.net.http.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FlightScraper implements FlightFeeder {

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
        // AeroDataBox permite maximo 12h por llamada, dividimos el dia en dos mitades
        // Pequeno sleep entre llamadas para no superar el rate limit por segundo
        flights.addAll(fetchFlights(depIata, arrIata, date, "T00:00", "T11:59"));
        sleep(2000);
        flights.addAll(fetchFlights(depIata, arrIata, date, "T12:00", "T23:59"));
        return flights;
    }

    private void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private List<FlightInfo> fetchFlights(String depIata, String arrIata, LocalDate date,
                                          String fromSuffix, String toSuffix) {
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
                System.err.println("AeroDataBox error [" + response.statusCode() + "] para "
                        + depIata + " -> " + arrIata + " el " + date + fromSuffix + ": " + response.body());
                return flights;
            }

            JsonObject root = gson.fromJson(response.body(), JsonObject.class);
            JsonArray departures = root.getAsJsonArray("departures");
            if (departures == null) return flights;

            for (JsonElement el : departures) {
                JsonObject f = el.getAsJsonObject();

                JsonObject arrival = f.getAsJsonObject("arrival");
                if (arrival == null) continue;

                JsonObject arrAirport = arrival.getAsJsonObject("airport");
                if (arrAirport == null || !arrAirport.has("iata")) continue;

                String arrIataActual = arrAirport.get("iata").getAsString();
                if (!arrIataActual.equalsIgnoreCase(arrIata)) continue;

                String flightNumber = f.has("number") ? f.get("number").getAsString().replace(" ", "") : "N/A";

                JsonObject airlineObj = f.getAsJsonObject("airline");
                String airline = (airlineObj != null && airlineObj.has("name"))
                        ? airlineObj.get("name").getAsString() : "N/A";

                JsonObject dep = f.getAsJsonObject("departure");
                String depTime = "N/A";
                if (dep != null && dep.has("scheduledTime")) {
                    JsonObject schedDep = dep.getAsJsonObject("scheduledTime");
                    if (schedDep.has("local")) depTime = schedDep.get("local").getAsString();
                    else if (schedDep.has("utc")) depTime = schedDep.get("utc").getAsString();
                }

                String arrTime = "N/A";
                if (arrival.has("scheduledTime")) {
                    JsonObject schedArr = arrival.getAsJsonObject("scheduledTime");
                    if (schedArr.has("local")) arrTime = schedArr.get("local").getAsString();
                    else if (schedArr.has("utc")) arrTime = schedArr.get("utc").getAsString();
                }

                String status = f.has("status") ? f.get("status").getAsString().toUpperCase() : "SCHEDULED";

                flights.add(new FlightInfo(flightNumber, depIata, arrIata, depTime, arrTime, status, airline, capturedAt));
            }

            System.out.println(">>> [AeroDataBox] " + flights.size()
                    + " vuelos " + depIata + " -> " + arrIata + " para el " + date + fromSuffix);

        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error en AeroDataBox [" + depIata + " -> " + arrIata
                    + " / " + date + fromSuffix + "]: " + e.getMessage());
        }

        return flights;
    }
}