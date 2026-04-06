package org.project.persistence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AirportManager {
    private static final String URL = "jdbc:sqlite:football.db";

    private record TeamData(String name, String city, String code) {
    }

    public void seedAirports() {
        List<TeamData> teams = new ArrayList<>();

        teams.add(new TeamData("Arsenal FC", "Londres", "LHR"));
        teams.add(new TeamData("FC Bayern München", "Múnich", "MUC"));
        teams.add(new TeamData("Liverpool FC", "Liverpool", "LPL"));
        teams.add(new TeamData("Tottenham Hotspur FC", "Londres", "STN"));
        teams.add(new TeamData("FC Barcelona", "Barcelona", "BCN"));
        teams.add(new TeamData("Chelsea FC", "Londres", "LGW"));
        teams.add(new TeamData("Sporting CP", "Lisboa", "LIS"));
        teams.add(new TeamData("Manchester City FC", "Manchester", "MAN"));
        teams.add(new TeamData("Real Madrid CF", "Madrid", "MAD"));
        teams.add(new TeamData("Inter Milan", "Milán", "MXP"));
        teams.add(new TeamData("Paris Saint-Germain FC", "París", "CDG"));
        teams.add(new TeamData("Newcastle United FC", "Newcastle", "NCL"));
        teams.add(new TeamData("Juventus FC", "Turín", "TRN"));
        teams.add(new TeamData("Atlético de Madrid", "Madrid", "MAD"));
        teams.add(new TeamData("Atalanta BC", "Bérgamo", "BGY"));
        teams.add(new TeamData("Bayer 04 Leverkusen", "Leverkusen", "CGN"));
        teams.add(new TeamData("Borussia Dortmund", "Dortmund", "DTM"));
        teams.add(new TeamData("Olympiacos FC", "El Pireo", "ATH"));
        teams.add(new TeamData("Club Brugge KV", "Brujas", "OST"));
        teams.add(new TeamData("Galatasaray SK", "Estambul", "IST"));
        teams.add(new TeamData("AS Monaco FC", "Mónaco", "NCE")); // Aeropuerto de Niza
        teams.add(new TeamData("Qarabağ FK", "Bakú", "GYD"));
        teams.add(new TeamData("FK Bodø/Glimt", "Bodø", "BOO"));
        teams.add(new TeamData("SL Benfica", "Lisboa", "LIS"));
        teams.add(new TeamData("Olympique de Marseille", "Marsella", "MRS"));
        teams.add(new TeamData("Pafos FC", "Pafos", "PFO"));
        teams.add(new TeamData("Union Saint-Gilloise", "Bruselas", "BRU"));
        teams.add(new TeamData("PSV Eindhoven", "Eindhoven", "EIN"));
        teams.add(new TeamData("Athletic Club", "Bilbao", "BIO"));
        teams.add(new TeamData("SSC Napoli", "Nápoles", "NAP"));
        teams.add(new TeamData("FC Copenhagen", "Copenhague", "CPH"));
        teams.add(new TeamData("AFC Ajax", "Ámsterdam", "AMS"));
        teams.add(new TeamData("Eintracht Frankfurt", "Fráncfort", "FRA"));
        teams.add(new TeamData("SK Slavia Praha", "Praga", "PRG"));
        teams.add(new TeamData("Villarreal CF", "Villarreal", "VLC"));
        teams.add(new TeamData("FC Kairat", "Almaty", "ALA"));

        String sql = "INSERT OR REPLACE INTO team_airports (team_name, city, airport_code) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (TeamData team : teams) {
                pstmt.setString(1, team.name());
                pstmt.setString(2, team.city());
                pstmt.setString(3, team.code());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("Los 36 equipos de Champions cargados correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al cargar la lista de Champions: " + e.getMessage());
        }
    }
}