package org.project.businessunit.core;

import org.project.businessunit.model.Flight;
import org.project.businessunit.model.Match;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Datamart {
    private final Map<String, List<Match>> matchesByDate = new ConcurrentHashMap<>();
    private final Map<String, List<Flight>> flightsByAirport = new ConcurrentHashMap<>();

    public void addMatch(Match match) {
        String dateKey = match.matchday().substring(0, 10);
        matchesByDate.computeIfAbsent(dateKey, k -> new ArrayList<>()).add(match);
    }
    //public void addMatch(Match match) {
    //    String dateKey = match.matchday().substring(0, 10);
    //    List<Match> matches = matchesByDate.computeIfAbsent(dateKey, k -> new ArrayList<>());

    //    boolean exists = matches.stream().anyMatch(m ->
    //            m.localTeam().equals(match.localTeam()) &&
    //                    m.visitorTeam().equals(match.visitorTeam()) &&
    //                    m.matchday().equals(match.matchday())
    //    );

    //    if (!exists) {
    //        matches.add(match);
    //    }
    //}

    public void addFlight(Flight flight) {
        flightsByAirport.computeIfAbsent(flight.destination(), k -> new ArrayList<>()).add(flight);
    }

    //public void addFlight(Flight flight) {
    //    List<Flight> flights = flightsByAirport.computeIfAbsent(flight.destination(), k -> new ArrayList<>());

    //    boolean exists = flights.stream().anyMatch(f ->
    //            f.flightNumber().equals(flight.flightNumber()) &&
    //                    f.departureTime().equals(flight.departureTime())
    //    );

    //    if (!exists) {
    //        flights.add(flight);
    //    }
    //}

    public List<Match> getMatches(String date) {
        return matchesByDate.getOrDefault(date, Collections.emptyList());
    }

    public List<Flight> getFlights(String airportCode) {
        return flightsByAirport.getOrDefault(airportCode, Collections.emptyList());
    }

    public Set<String> getAvailableDates() {
        return matchesByDate.keySet();
    }
}