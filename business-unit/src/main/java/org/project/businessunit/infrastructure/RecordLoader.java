package org.project.businessunit.infrastructure;

import com.google.gson.Gson;
import org.project.businessunit.core.Datamart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class RecordLoader {
    private final String eventStorePath = "eventstore"; // Ruta raíz del Sprint 2
    private final Datamart datamart;
    private final Gson gson = new Gson();

    public RecordLoader(Datamart datamart) {
        this.datamart = datamart;
    }

    public void loadHistoricalData() {
        System.out.println(">>> Cargando datos históricos desde el Event Store...");

        // 1. Cargar Partidos
        loadFromTopic("Football");

        // 2. Cargar Vuelos
        loadFromTopic("Travel");

        System.out.println(">>> Carga histórica completada.");
    }

    private void loadFromTopic(String topic) {
        Path path = Paths.get(eventStorePath, topic);
        if (!Files.exists(path)) return;

        try (Stream<Path> walk = Files.walk(path)) {
            walk.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".events"))
                    .forEach(this::readFile);
        } catch (Exception e) {
            System.err.println("Error accediendo al Event Store de " + topic + ": " + e.getMessage());
        }
    }

    private void readFile(Path filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                Map event = gson.fromJson(line, Map.class);

                // Determinamos qué tipo de evento es por la carpeta
                if (filePath.toString().contains("Football")) {
                    Map match = (Map) event.get("match");
                    datamart.updateMatch(match);
                } else if (filePath.toString().contains("Travel")) {
                    Map flight = (Map) event.get("flight");
                    String dest = (String) flight.get("destination");
                    List<Map<String, Object>> list = datamart.getFlightsFor(dest);
                    list.add(flight);
                    datamart.updateFlights(dest, list);
                }
            }
        } catch (Exception e) {
            System.err.println("Error leyendo archivo " + filePath + ": " + e.getMessage());
        }
    }
}