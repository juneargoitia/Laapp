package org.project.infrastructure;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.project.core.FootballFeeder;
import org.project.model.Match;

import java.util.List;
import java.util.ArrayList;

public class FootballClient implements FootballFeeder {
    private static final String BASE_URL = "https://api.football-data.org/v4/competitions/";

    private static final String API_KEY = System.getenv("FOOTBALL_API_KEY");

    private final OkHttpClient client = new OkHttpClient();
    private final MatchParser parser = new MatchParser();

    @Override
    public List<Match> getMatches() {
        try {
            String json = fetchMatchesJson("CL");
            return parser.parse(json);
        } catch (Exception e) {
            System.err.println("Error obteniendo partidos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private String fetchMatchesJson(String competition) throws Exception {

        String finalUrl = BASE_URL + competition + "/matches?status=SCHEDULED";

        System.out.println("DEBUG: Buscando próximos partidos en -> " + finalUrl);

        Request request = new Request.Builder()
                .url(finalUrl)
                .addHeader("X-Auth-Token", API_KEY != null ? API_KEY : "TU_TOKEN_AQUI")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("Error API: " + response.code());
            }

            ResponseBody body = response.body();
            if (body == null) throw new Exception("Respuesta vacía de la API");

            String content = body.string();

            if (content.contains("\"matches\":[]")) {
                System.out.println("INFO: No hay partidos programados en los próximos días.");
                return fetchAllSeasonMatches(competition);
            }

            return content;
        }
    }

    private String fetchAllSeasonMatches(String competition) throws Exception {
        String url = BASE_URL + competition + "/matches?season=2024";
        System.out.println("DEBUG: Cargando histórico de temporada para tener datos: " + url);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Auth-Token", API_KEY)
                .build();

        try (Response res = client.newCall(request).execute()) {
            return res.body().string();
        }
    }
}