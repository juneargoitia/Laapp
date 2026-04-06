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

        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        JsonArray matchArray = root.getAsJsonArray("matches");

        for (JsonElement element : matchArray) {
            JsonObject m = element.getAsJsonObject();

            int id = m.get("id").getAsInt();
            String status = m.get("status").getAsString();
            String utcFullDate = m.get("utcDate").getAsString();
            String[] parts = utcFullDate.split("T");

            String matchday = parts[0];
            String matchDate = parts[1].replace("Z", "");

            String homeTeam = m.getAsJsonObject("homeTeam")
                    .get("name").getAsString();
            String awayTeam = m.getAsJsonObject("awayTeam")
                    .get("name").getAsString();

            JsonObject score = m.getAsJsonObject("score");
            JsonObject fullTime = score.getAsJsonObject("fullTime");
            int scoreHome = fullTime.get("home").isJsonNull() ? 0
                    : fullTime.get("home").getAsInt();
            int scoreAway = fullTime.get("away").isJsonNull() ? 0
                    : fullTime.get("away").getAsInt();

            String competition = m.getAsJsonObject("competition")
                    .get("name").getAsString();

            matches.add(new Match(id, homeTeam, awayTeam, status,
                    matchday, matchDate, competition,
                    capturedAt, scoreHome, scoreAway));
        }
        return matches;
    }
}
