package org.project.infrastructure;

import java.util.HashMap;
import java.util.Map;

public class AirportMapper {
    private final Map<String, String> airportMap = new HashMap<>();

    public AirportMapper() {
        initAirportMap();
    }

    private void initAirportMap() {
        airportMap.put("Arsenal FC", "LHR");
        airportMap.put("FC Bayern München", "MUC");
        airportMap.put("Liverpool FC", "LPL");
        airportMap.put("Tottenham Hotspur FC", "STN");
        airportMap.put("FC Barcelona", "BCN");
        airportMap.put("Chelsea FC", "LGW");
        airportMap.put("Sporting CP", "LIS");
        airportMap.put("Sporting Clube de Portugal", "LIS");
        airportMap.put("Manchester City FC", "MAN");
        airportMap.put("Real Madrid CF", "MAD");
        airportMap.put("Inter Milan", "MXP");
        airportMap.put("Paris Saint-Germain FC", "CDG");
        airportMap.put("Newcastle United FC", "NCL");
        airportMap.put("Juventus FC", "TRN");
        airportMap.put("Club Atlético de Madrid", "MAD");
        airportMap.put("Atalanta BC", "BGY");
        airportMap.put("Bayer 04 Leverkusen", "CGN");
        airportMap.put("Borussia Dortmund", "DTM");
        airportMap.put("Olympiacos FC", "ATH");
        airportMap.put("Club Brugge KV", "OST");
        airportMap.put("Galatasaray SK", "IST");
        airportMap.put("AS Monaco FC", "NCE");
        airportMap.put("Qarabağ FK", "GYD");
        airportMap.put("FK Bodø/Glimt", "BOO");
        airportMap.put("SL Benfica", "LIS");
        airportMap.put("Olympique de Marseille", "MRS");
        airportMap.put("Pafos FC", "PFO");
        airportMap.put("Union Saint-Gilloise", "BRU");
        airportMap.put("PSV Eindhoven", "EIN");
        airportMap.put("Athletic Club", "BIO");
        airportMap.put("SSC Napoli", "NAP");
        airportMap.put("FC Copenhagen", "CPH");
        airportMap.put("AFC Ajax", "AMS");
        airportMap.put("Eintracht Frankfurt", "FRA");
        airportMap.put("SK Slavia Praha", "PRG");
        airportMap.put("Villarreal CF", "VLC");
        airportMap.put("FC Kairat", "ALA");
    }

    public String getCode(String teamName) {
        return airportMap.getOrDefault(teamName, "N/A");
    }
}
