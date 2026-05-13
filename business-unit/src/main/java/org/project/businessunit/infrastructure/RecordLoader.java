package org.project.businessunit.infrastructure;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.project.businessunit.core.Datamart;
import org.project.businessunit.model.Flight;
import org.project.businessunit.model.Match;
import java.io.*;

public class RecordLoader {
    private final Datamart datamart;

    public RecordLoader(Datamart datamart) {
        this.datamart = datamart;
    }

    public void loadAll(String path) {
        File root = new File(path);
        if (!root.exists()) {
            System.out.println("(!) No se encontró la carpeta: " + path);
            return;
        }
        process(root);
    }

    private void process(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) process(child);
            }
        } else if (file.getName().endsWith(".events")) {
            loadFile(file);
        }
    }

    private void loadFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                JsonObject json = JsonParser.parseString(line).getAsJsonObject();
                if (!json.has("ss")) continue;

                String source = json.get("ss").getAsString();

                if (source.equals("football-feeder") && json.has("match")) {
                    JsonObject m = json.getAsJsonObject("match");
                    datamart.addMatch(new Match(
                            getString(m, "localTeam"),
                            getString(m, "visitorTeam"),
                            getString(m, "matchday"),
                            getString(m, "matchDate"),
                            m.has("matchStatus") ? m.get("matchStatus").getAsString() : "N/A",
                            getString(m,"competition"),
                            getString(m, "airportCode")
                    ));
                } else if ((source.equals("flight-feeder") || source.equals("TravelConsumer")) && json.has("flight")) {
                    JsonObject f = json.getAsJsonObject("flight");
                    double price = f.has("price") ? f.get("price").getAsDouble() : 0.0;
                    datamart.addFlight(new Flight(
                            getString(f, "flightNumber"),
                            getString(f, "airline"),
                            getString(f, "origin"),
                            getString(f, "destination"),
                            getString(f, "departureTime"),
                            getString(f, "arrivalTime"),
                            getString(f, "status"),
                            f.has("capturedAt") ? f.get("capturedAt").getAsString() : json.get("ts").getAsString(),
                            price
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Error procesando archivo " + file.getName() + ": " + e.getMessage());
        }
    }

    private String getString(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsString() : "Unknown";
    }
}