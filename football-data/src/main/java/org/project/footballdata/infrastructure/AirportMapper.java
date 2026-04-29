package org.project.footballdata.infrastructure;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class AirportMapper {
    private final Map<String, String> airportMap = new HashMap<>();

    public AirportMapper() {
        loadFromExternalFile();
    }

    private void loadFromExternalFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("airports.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    airportMap.put(parts[0].trim(), parts[1].trim());
                }
            }
            System.out.println("INFO: Cargados " + airportMap.size() + " aeropuertos desde airports.txt");
        } catch (Exception e) {
            System.err.println("ERROR: No se pudo leer airports.txt. Usando mapa vacío.");
        }
    }

    public String getCode(String teamName, String date) {
        if ("2026-05-30".equals(date)) {
            return "BUD";
        }
        return airportMap.getOrDefault(teamName, "N/A");
    }
    public String getCode(String teamName) {
        return getCode(teamName, "");
    }
}
