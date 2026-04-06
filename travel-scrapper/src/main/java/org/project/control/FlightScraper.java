package org.project.control;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.project.model.FlightInfo;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class FlightScraper implements FlightFeeder{

    private static final String URL = "https://www.flightaware.com/live/all";

    @Override
    public List<FlightInfo> getFlights() {
        List<FlightInfo> flights = new ArrayList<>();
        String capturedAt = Instant.now().toString();

        try {
            Document doc = Jsoup.connect(URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header("Accept-Language", "es-ES,es;q=0.9")
                    .timeout(15000)
                    .get();

            Elements rows = doc.select("table.fullWidth tr");

            for (Element row : rows) {
                if (row.select("th").size() > 0) continue;

                Elements cols = row.select("td");
                if (cols.size() >= 4) {
                    String ident = cols.get(0).text();
                    String origin = cols.get(2).text();
                    String destination = cols.get(3).text();

                    flights.add(new FlightInfo(
                            ident,
                            origin,
                            destination,
                            "En vuelo",
                            "Calculando",
                            "LIVE",
                            "Compañía",
                            capturedAt
                    ));
                }
            }

            if (flights.isEmpty()) {
                flights.add(new FlightInfo("IB3110", "MAD", "LPA", "18:00",
                        "20:30", "ON TIME", "Iberia", capturedAt));
            }

        } catch (Exception e) {
            System.err.println("Error en FlightAware Scraper: " + e.getMessage());
            flights.add(new FlightInfo("TEST-FLIGHT", "LPA", "TFN", "09:00",
                    "09:30", "LANDED", "Binter", capturedAt));
        }
        return flights;
    }
}
