package org.project.travelscrapper.travelscrapper.core;

import org.project.travelscrapper.travelscrapper.model.FlightInfo;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class FlightController {
    private final FlightFeeder feeder;
    private final FlightStore store;

    public FlightController(FlightFeeder feeder, FlightStore store) {
        this.feeder = feeder;
        this.store = store;
    }

    public void execute(List<String> destinations) {
        LocalDate today = LocalDate.now();

        for (String destination : destinations) {
            if (destination.equals("MAD") || destination.equals("N/A")) continue;

            System.out.println("\n>>> Scrapeando vuelos para destino: " + destination);
            List<FlightInfo> allFlights = new ArrayList<>();

            for (int i = 0; i < 60; i++) {
                LocalDate date = today.plusDays(i);
                DayOfWeek day = date.getDayOfWeek();

                if (day == DayOfWeek.MONDAY) {
                    allFlights.addAll(feeder.getFlights(destination, date));
                }
                if (day == DayOfWeek.THURSDAY) {
                    allFlights.addAll(feeder.getFlights("MAD_RETURN_" + destination, date));
                }

                if (isFinalSeason(date)) {
                    if (day == DayOfWeek.FRIDAY) {
                        System.out.println("  [ESPECIAL FINAL] Ida Viernes: " + date);
                        allFlights.addAll(feeder.getFlights(destination, date));
                    }
                    if (day == DayOfWeek.SUNDAY) {
                        System.out.println("  [ESPECIAL FINAL] Vuelta Domingo: " + date);
                        allFlights.addAll(feeder.getFlights("MAD_RETURN_" + destination, date));
                    }
                }
            }

            if (!allFlights.isEmpty()) {
                store.save(allFlights);
            }
        }
        }
    private boolean isFinalSeason(LocalDate date) {
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();

        if (month == 5 && day >= 15) return true;
        if (month == 6 && day <= 15) return true;
        return false;
    }
}
