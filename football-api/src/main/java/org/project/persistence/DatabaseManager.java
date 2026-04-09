package org.project.persistence;

import org.project.control.FootballStore;
import org.project.model.Match;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManager implements FootballStore {
    private static final String URL = "jdbc:sqlite:football.db";
    private final Map<String, String> airportMap = new HashMap<>();

    public DatabaseManager() {
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

    private void createTable() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS matches (
                    id INTEGER PRIMARY KEY,
                    home_team TEXT,
                    away_team TEXT,
                    status TEXT,
                    matchday TEXT,
                    utc_date TEXT,
                    competition TEXT,
                    captured_at TEXT,
                    score_home INTEGER,
                    score_away INTEGER,
                    airport_code TEXT
                );
                """;
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    @Override
    public void save(List<Match> matches) {
        try {
            createTable();
            String sql = """
                    INSERT OR REPLACE INTO matches
                    (id, home_team, away_team, status, matchday,
                     utc_date, competition, captured_at, score_home, score_away, airport_code)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;
            try (Connection conn = DriverManager.getConnection(URL);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (Match m : matches) {
                    String code = airportMap.getOrDefault(m.getLocalTeam(), "N/A");

                    stmt.setInt(1, m.getId());
                    stmt.setString(2, m.getLocalTeam());
                    stmt.setString(3, m.getVisitorTeam());
                    stmt.setString(4, m.getMatchStatus());
                    stmt.setString(5, m.getMatchday());
                    stmt.setString(6, m.getMatchDate());
                    stmt.setString(7, m.getCompetition());
                    stmt.setString(8, m.getCapturedAt());
                    stmt.setInt(9, m.getScoreHome());
                    stmt.setInt(10, m.getScoreAway());
                    stmt.setString(11, code);
                    stmt.addBatch();
                }
                stmt.executeBatch();
                System.out.println("Partidos y aeropuertos integrados guardados correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("Error en SQLite: " + e.getMessage());
        }
    }
}