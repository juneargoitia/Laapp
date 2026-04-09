package org.project.parser;

import com.google.gson.*;
import org.project.model.Match;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MatchParser {
    public List<Match> parse(String json) {
        List<Match> matches = new ArrayList<>();
        String capturedAt = Instant.now().toString();

        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();

            String competitionName = "Champions League";
            if (root.has("competition") && root.getAsJsonObject("competition").has("name")) {
                competitionName = root.getAsJsonObject("competition").get("name").getAsString();
            }

            JsonArray matchArray = root.getAsJsonArray("matches");

            for (JsonElement element : matchArray) {
                JsonObject m = element.getAsJsonObject();

                int id = m.get("id").getAsInt();
                String status = m.get("status").getAsString();
                String utcFullDate = m.get("utcDate").getAsString();

                String[] parts = utcFullDate.split("T");
                String matchday = parts[0];
                String matchTime = parts[1].replace("Z", "");

                String homeTeam = "TBD";
                if (m.has("homeTeam") && !m.get("homeTeam").isJsonNull()) {
                    JsonElement nameElement = m.getAsJsonObject("homeTeam").get("name");
                    if (nameElement != null && !nameElement.isJsonNull()) {
                        homeTeam = nameElement.getAsString();
                    }
                }

                String awayTeam = "TBD";
                if (m.has("awayTeam") && !m.get("awayTeam").isJsonNull()) {
                    JsonElement nameElement = m.getAsJsonObject("awayTeam").get("name");
                    if (nameElement != null && !nameElement.isJsonNull()) {
                        awayTeam = nameElement.getAsString();
                    }
                }

                int scoreHome = 0;
                int scoreAway = 0;
                if (m.has("score") && !m.get("score").isJsonNull()) {
                    JsonObject fullTime = m.getAsJsonObject("score").getAsJsonObject("fullTime");
                    if (fullTime != null) {
                        scoreHome = fullTime.get("home").isJsonNull() ? 0 : fullTime.get("home").getAsInt();
                        scoreAway = fullTime.get("away").isJsonNull() ? 0 : fullTime.get("away").getAsInt();
                    }
                }

                matches.add(new Match(id, homeTeam, awayTeam, status,
                        matchday, matchTime, competitionName,
                        capturedAt, scoreHome, scoreAway));
            }
        } catch (Exception e) {
            System.err.println("Error procesando el JSON: " + e.getMessage());
            e.printStackTrace();
        }
        return matches;
    }
}