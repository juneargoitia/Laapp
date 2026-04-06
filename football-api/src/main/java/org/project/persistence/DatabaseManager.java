package org.project.persistence;

import org.project.control.FootballStore;
import org.project.model.Match;
import java.sql.*;
import java.util.List;

public class DatabaseManager implements FootballStore {
    private static final String URL = "jdbc:sqlite:football.db";

    private void createTable() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS matches (
                    id INTEGER,
                    home_team TEXT,
                    away_team TEXT,
                    status TEXT,
                    matchday INTEGER,
                    utc_date TEXT,
                    competition TEXT,
                    captured_at TEXT,
                    score_home INTEGER,
                    score_away INTEGER
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
                    INSERT INTO matches
                    (id, home_team, away_team, status, matchday,
                     utc_date, competition, captured_at, score_home, score_away)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;
            try (Connection conn = DriverManager.getConnection(URL);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (Match m : matches) {
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
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        } catch (SQLException e) {
            System.err.println("Error en SQLite: " + e.getMessage());
        }
    }
}