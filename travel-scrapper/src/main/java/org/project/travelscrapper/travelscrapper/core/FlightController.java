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
                        allFlights.addAll(feeder.getFlights(code, date));
                    }
                    if (day == DayOfWeek.THURSDAY) {
                        allFlights.addAll(feeder.getFlights("MAD_RETURN_" + code, date));
                    }
                }
                else if (type.equalsIgnoreCase("final")) {
                    if (date.isAfter(LocalDate.of(2026, 5, 19)) && date.isBefore(LocalDate.of(2026, 6, 16))) {
                        if (day == DayOfWeek.FRIDAY) {
                            allFlights.addAll(feeder.getFlights(code, date));
                        }

                        if (day == DayOfWeek.SUNDAY) {
                            allFlights.addAll(feeder.getFlights("MAD_RETURN_" + code, date));
                        }
                    }
                }
            }

            if (!allFlights.isEmpty()) {
                store.save(allFlights);
            }
        }
    }
}
