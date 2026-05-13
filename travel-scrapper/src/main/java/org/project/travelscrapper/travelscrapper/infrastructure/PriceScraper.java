package org.project.travelscrapper.travelscrapper.infrastructure;

import com.google.gson.*;
import java.io.ByteArrayInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class PriceScraper {
    private final HttpClient http = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();

    private static final String HOST = "priceline-com-provider.p.rapidapi.com";

    private String getApiKey() {
        String key = System.getenv("FLIGHT_API_KEY");
        return (key != null && !key.isEmpty()) ? key.trim() : "TU_KEY_AQUI";
    }

    public Map<String, Double> fetchPricesByFlight(String origin, String dest, LocalDate date) {
        Map<String, Double> prices = new HashMap<>();
        try {
            String url = String.format(
                    "https://%s/v2/flight/departures?sid=IB3641&destination_airport_code=%s&departure_date=%s&adults=1&origin_airport_code=%s&currency=EUR",
                    HOST, dest, date.toString(), origin
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("x-rapidapi-key", getApiKey())
                    .header("x-rapidapi-host", HOST)
                    .GET()
                    .build();

            HttpResponse<byte[]> response = http.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() != 200) {
                System.err.println(">>> ERROR API (" + response.statusCode() + ")");
                return prices;
            }

            String body;
            try (GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(response.body()));
                 InputStreamReader reader = new InputStreamReader(gzip, StandardCharsets.UTF_8);
                 BufferedReader br = new BufferedReader(reader)) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                body = sb.toString();
            } catch (Exception e) {
                body = new String(response.body(), StandardCharsets.UTF_8);
            }

            JsonObject json = JsonParser.parseString(body).getAsJsonObject();
            JsonObject itineraryData = json
                    .getAsJsonObject("getAirFlightDepartures")
                    .getAsJsonObject("results")
                    .getAsJsonObject("result")
                    .getAsJsonObject("itinerary_data");

            if (itineraryData == null) return prices;

            for (Map.Entry<String, JsonElement> entry : itineraryData.entrySet()) {
                try {
                    JsonObject itinerary = entry.getValue().getAsJsonObject();
                    double price = itinerary
                            .getAsJsonObject("price_details")
                            .get("display_total_fare").getAsDouble();

                    JsonObject sliceData = itinerary.getAsJsonObject("slice_data");
                    if (sliceData == null) continue;

                    for (Map.Entry<String, JsonElement> sliceEntry : sliceData.entrySet()) {
                        JsonObject slice = sliceEntry.getValue().getAsJsonObject();
                        JsonObject flightData = slice.getAsJsonObject("flight_data");
                        if (flightData == null) continue;

                        for (Map.Entry<String, JsonElement> flightEntry : flightData.entrySet()) {
                            try {
                                JsonObject flight = flightEntry.getValue().getAsJsonObject();
                                JsonObject info = flight.getAsJsonObject("info");
                                String airlineCode = info.get("marketing_airline_code").getAsString();
                                String flightNumber = info.get("flight_number").getAsString();
                                String fullCode = airlineCode + flightNumber;

                                prices.merge(fullCode, price, Math::min);
                            } catch (Exception ignored) {}
                        }
                    }
                } catch (Exception ignored) {}
            }

            System.out.println(">>> Precios obtenidos para " + origin + "-" + dest + ": " + prices.size() + " vuelos");

        } catch (Exception e) {
            System.err.println("Fallo en PriceScraper: " + e.getMessage());
        }
        return prices;
    }

    public double fetchRealPrice(String origin, String dest, LocalDate date) {
        Map<String, Double> prices = fetchPricesByFlight(origin, dest, date);
        if (!prices.isEmpty()) {
            return prices.values().stream().mapToDouble(Double::doubleValue).min().orElse(180.0);
        }
        return 180.0;
    }
}