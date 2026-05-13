package org.project.businessunit.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RecommendedTrip {
    private final Match match;
    private final List<Flight> departureFlights;
    private final List<Flight> returnFlights;
    private final String dayIda;
    private final String dayVuelta;

    public RecommendedTrip(Match match) {
        this.match = match;
        this.departureFlights = new ArrayList<>();
        this.returnFlights = new ArrayList<>();

        String matchDateStr = match.matchday().substring(0, 10);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate matchDate = LocalDate.parse(matchDateStr, formatter);

        if (matchDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
            this.dayIda = matchDate.minusDays(1).format(formatter);
            this.dayVuelta = matchDate.plusDays(1).format(formatter);
        } else {
            LocalDate ida = matchDate;
            while (ida.getDayOfWeek() != DayOfWeek.MONDAY) ida = ida.minusDays(1);
            LocalDate vuelta = matchDate;
            while (vuelta.getDayOfWeek() != DayOfWeek.THURSDAY) vuelta = vuelta.plusDays(1);
            this.dayIda = ida.format(formatter);
            this.dayVuelta = vuelta.format(formatter);
        }
    }

    public Match getMatch() { return match; }
    public List<Flight> getDepartureFlights() { return departureFlights; }
    public List<Flight> getReturnFlights() { return returnFlights; }

    public void addIfFits(Flight flight) {
        String flightDate = flight.departureTime().substring(0, 10);

        if (flight.origin().equals("MAD") &&
                flight.destination().equalsIgnoreCase(match.airportCode()) &&
                flightDate.equals(dayIda)) {
            if (!departureFlights.contains(flight)) departureFlights.add(flight);
        }

        if (flight.origin().equalsIgnoreCase(match.airportCode()) &&
                flight.destination().equals("MAD") &&
                flightDate.equals(dayVuelta)) {
            if (!returnFlights.contains(flight)) returnFlights.add(flight);
        }
    }
}