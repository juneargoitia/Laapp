package org.project.businessunit.core;

import org.project.businessunit.model.Flight;
import org.project.businessunit.model.Match;
import org.project.businessunit.model.RecommendedTrip;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Datamart {
    private final Map<String, List<Match>> matchesByDate = new ConcurrentHashMap<>();
    private final Map<String, RecommendedTrip> recommendedTrips = new ConcurrentHashMap<>();
    private final List<Flight> allFlights = new CopyOnWriteArrayList<>();

    public void addMatch(Match match) {
        String dateKey = match.matchday().substring(0, 10);
        matchesByDate.computeIfAbsent(dateKey, k -> new ArrayList<>()).add(match);

        String tripKey = match.localTeam() + "-" + match.visitorTeam() + "-" + dateKey;

        RecommendedTrip trip = new RecommendedTrip(match);
        for (Flight f : allFlights) {
            trip.addIfFits(f);
        }
        recommendedTrips.put(tripKey, trip);
    }

    public void addFlight(Flight flight) {
        allFlights.add(flight);
        for (RecommendedTrip trip : recommendedTrips.values()) {
            trip.addIfFits(flight);
        }
    }

    public RecommendedTrip getTrip(Match match) {
        String dateKey = match.matchday().substring(0, 10);
        String tripKey = match.localTeam() + "-" + match.visitorTeam() + "-" + dateKey;
        return recommendedTrips.get(tripKey);
    }

    public List<Match> getMatches(String date) {
        return matchesByDate.getOrDefault(date, Collections.emptyList());
    }

    public Set<String> getAvailableDates() {
        return matchesByDate.keySet();
    }
}