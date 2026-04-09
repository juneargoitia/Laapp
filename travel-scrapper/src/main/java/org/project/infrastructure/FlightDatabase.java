package org.project.infrastructure;

import org.project.core.FlightStore;
import org.project.model.FlightInfo;
import java.util.List;
import java.sql.*;

public class FlightDatabase implements FlightStore {
    private static final String URL = "jdbc:sqlite:flights.db";

    public FlightDatabase() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS flights (
                    flight_number TEXT,
                    origin TEXT,
                    destination TEXT,
                    departure_time TEXT,
                    arrival_time TEXT,
                    status TEXT,
                    airline TEXT,
                    captured_at TEXT
                )
                """);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(List<FlightInfo> flights) {
        String sql = "INSERT INTO flights VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (FlightInfo f : flights) {
                pstmt.setString(1, f.getFlightNumber());
                pstmt.setString(2, f.getOrigin());
                pstmt.setString(3, f.getDestination());
                pstmt.setString(4, f.getDepartureTime());
                pstmt.setString(5, f.getArrivalTime());
                pstmt.setString(6, f.getFlightStatus());
                pstmt.setString(7, f.getAirline());
                pstmt.setString(8, f.getCapturedAt());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println(">>> Base de datos de VUELOS actualizada correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al guardar vuelos: " + e.getMessage());
        }
    }
}
