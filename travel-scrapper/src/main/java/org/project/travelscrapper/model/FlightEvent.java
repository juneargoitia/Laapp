package org.project.travelscrapper.model;

public class FlightEvent {
    private final String ts;
    private final String ss;
    private final FlightInfo flight;

    public FlightEvent(FlightInfo flight) {
        this.ts = flight.getCapturedAt();
        this.ss = "flight-feeder";
        this.flight = flight;
    }

    public String getTs() { return ts; }
    public String getSs() { return ss; }
    public FlightInfo getFlight() { return flight; }
}
