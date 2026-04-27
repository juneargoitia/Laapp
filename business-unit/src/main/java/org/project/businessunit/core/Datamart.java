package org.project.businessunit.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Datamart {
    private final Map<String, Map<String, Object>> matches = new ConcurrentHashMap<>();

    private final Map<String, List<Map<String, Object>>> flights = new ConcurrentHashMap<>();

    public void updateMatch(Map<String, Object> matchData) {
        String id = String.valueOf(matchData.get("id"));
        matches.put(id, matchData);
    }

    public void updateFlights(String destination, List<Map<String, Object>> flightList) {
        flights.put(destination, flightList);
    }

    public Map<String, Map<String, Object>> getMatches() {
        return matches;
    }

    public List<Map<String, Object>> getFlightsFor(String airportCode) {
        return flights.getOrDefault(airportCode, new ArrayList<>());
    }
}
