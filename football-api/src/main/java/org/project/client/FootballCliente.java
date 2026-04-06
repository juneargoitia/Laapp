package org.project.client;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.project.control.FootballFeeder;
import org.project.model.Match;
import org.project.parser.MatchParser;
import java.util.List;
import java.util.ArrayList;

public class FootballCliente implements FootballFeeder {
    private static final String BASE_URL = "https://api.football-data.org/v4/";
    private static final String API_KEY = "91066f0175b14ac696f23ecb7e68e792";
    private final OkHttpClient client = new OkHttpClient();
    private final MatchParser parser = new MatchParser();

    @Override
    public List<Match> getMatches() {
        try {
            String json = fetchMatchesJson("PD");
            return parser.parse(json);
        } catch (Exception e) {
            System.err.println("Error obteniendo partidos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private String fetchMatchesJson(String competition) throws Exception {
        Request request = new Request.Builder()
                .url(BASE_URL + "competitions/" + competition + "/matches")
                .addHeader("X-Auth-Token", API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new Exception("Error API: " + response.code());
            ResponseBody body = response.body();
            if (body == null)
                throw new Exception("Respuesta vacía de la API");
            return body.string();
        }
    }
}