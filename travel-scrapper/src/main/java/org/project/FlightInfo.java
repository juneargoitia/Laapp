package org.project;

public class FlightInfo {
    private String flight_number;
    private String origin;
    private String destination;
    private String departure_time;
    private String arrival_time;
    private String flight_status;
    private String captured_at;

    public FlightInfo(
            String flight_number, String origin, String destination,
            String departure_time, String arrival_time, String flight_status, String captured_at
    ) {
        this.flight_number = flight_number;
        this.origin = origin;
        this.destination = destination;
        this.departure_time = departure_time;
        this.arrival_time = arrival_time;
        this.flight_status = flight_status;
        this.captured_at = captured_at;
    }

    public String getFlightNumber() {
        return flight_number;
    }
    public String getOrigin() {
        return origin;
    }
    public String getDestination() {
        return destination;
    }
    public String getDepartureTime() {
        return departure_time;
    }
    public String getArrivalTime() {
        return arrival_time;
    }
    public String getStatus() {
        return flight_status;
    }
    public String getCapturedAt() {
        return captured_at;
    }
}