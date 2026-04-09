package org.project.infrastructure;

import org.project.core.FootballStore;
import org.project.model.Match;
import java.sql.*;
import java.util.List;

public class FootballDatabase implements FootballStore {
    private static final String URL = "jdbc:sqlite:football.db";
    private final AirportMapper mapper;

    public FootballDatabase() {
        this.mapper = new AirportMapper();
        createTable();
    }

    private void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS matches (
                    db_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    match_id INTEGER,
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
        } catch (SQLException e) {
            System.err.println("Error al crear la tabla matches: " + e.getMessage());
        }
    }

    @Override
    public void save(List<Match> matches) {
        String sql = """
                INSERT INTO matches
                    (match_id, home_team, away_team, status, matchday, utc_date,
                     competition, captured_at, score_home, score_away, airport_code)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Match m : matches) {
                String code = mapper.getCode(m.getLocalTeam());

                pstmt.setInt(1, m.getId());
                pstmt.setString(2, m.getLocalTeam());
                pstmt.setString(3, m.getVisitorTeam());
                pstmt.setString(4, m.getMatchStatus());
                pstmt.setString(5, m.getMatchday());
                pstmt.setString(6, m.getMatchDate());
                pstmt.setString(7, m.getCompetition());
                pstmt.setString(8, m.getCapturedAt());
                pstmt.setInt(9, m.getScoreHome());
                pstmt.setInt(10, m.getScoreAway());
                pstmt.setString(11, code);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("Partidos y aeropuertos integrados guardados correctamente.");

        }catch (SQLException e) {
            System.err.println("Error en SQLite: " + e.getMessage());
        }
    }
}