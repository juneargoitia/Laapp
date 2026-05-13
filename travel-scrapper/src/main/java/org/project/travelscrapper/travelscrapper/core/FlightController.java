package org.project.travelscrapper.travelscrapper.core;

import org.project.travelscrapper.travelscrapper.infrastructure.PriceScraper;
import org.project.travelscrapper.travelscrapper.model.FlightInfo;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class FlightController {
    private final FlightFeeder feeder;
    private final FlightStore store;
    private final PriceScraper priceScraper;

    public FlightController(FlightFeeder feeder, FlightStore store) {
        this.feeder = feeder;
        this.store = store;
        this.priceScraper = new PriceScraper();
    }



    public void execute(List<DestinationInfo> destinations) {
        LocalDate today = LocalDate.now();

        for (DestinationInfo dest : destinations) {
            String code = dest.getCode();
            String type = dest.getType();

            if (code.equals("MAD") || code.equals("N/A")) continue;

            List<FlightInfo> allFlights = new ArrayList<>();

            for (int i = 0; i < 60; i++) {
                LocalDate date = today.plusDays(i);
                DayOfWeek day = date.getDayOfWeek();

                if (type.equalsIgnoreCase("normal") && date.isBefore(LocalDate.of(2026, 5, 16))) {
                    if (day == DayOfWeek.MONDAY) {
                        allFlights.addAll(processFlightsWithPrice(code, date));
                    }
                    if (day == DayOfWeek.THURSDAY) {
                        allFlights.addAll(processFlightsWithPrice("MAD_RETURN_" + code, date));
                    }
                } else if (type.equalsIgnoreCase("final")) {
                    if (date.isAfter(LocalDate.of(2026, 5, 19)) && date.isBefore(LocalDate.of(2026, 6, 16))) {
                        if (day == DayOfWeek.FRIDAY) {
                            allFlights.addAll(processFlightsWithPrice(code, date));
                        }

                        if (day == DayOfWeek.SUNDAY) {
                            allFlights.addAll(processFlightsWithPrice("MAD_RETURN_" + code, date));
                        }
                    }
                }
            }

            if (!allFlights.isEmpty()) {
                store.save(allFlights);
            }
        }
    }
    private List<FlightInfo> processFlightsWithPrice(String destinationCode, LocalDate date) {
        List<FlightInfo> rawFlights = feeder.getFlights(destinationCode, date);
        List<FlightInfo> flightsWithPrice = new ArrayList<>();

        if (rawFlights.isEmpty()) return flightsWithPrice;

        String origin = destinationCode.startsWith("MAD_RETURN_") ? destinationCode.replace("MAD_RETURN_", "") : "MAD";
        String destination = destinationCode.startsWith("MAD_RETURN_") ? "MAD" : destinationCode;

        Map<String, Double> priceMap = priceScraper.fetchPricesByFlight(origin, destination, date);
        double defaultPrice = priceMap.values().stream().mapToDouble(Double::doubleValue).min().orElse(180.0);

        for (FlightInfo f : rawFlights) {
            double price = priceMap.getOrDefault(f.getFlightNumber(), defaultPrice);

            System.out.println(">>> Vuelo: " + f.getFlightNumber() + " | Precio: " + price + (priceMap.containsKey(f.getFlightNumber()) ? " (EXACTO)" : " (DEFAULT)"));

            flightsWithPrice.add(new FlightInfo(
                    f.getFlightNumber(),
                    f.getOrigin(),
                    f.getDestination(),
                    f.getDepartureTime(),
                    f.getArrivalTime(),
                    f.getFlightStatus(),
                    f.getAirline(),
                    f.getCapturedAt(),
                    price
            ));
        }
        return flightsWithPrice;
    }

}

