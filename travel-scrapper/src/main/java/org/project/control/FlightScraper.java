package org.project.control;

import org.project.model.FlightInfo;
import java.util.ArrayList;
import java.util.List;

public class FlightScraper implements FlightFeeder{
    @Override
    public List<FlightInfo> getFlights() {
        return new ArrayList<>();
    }
}
