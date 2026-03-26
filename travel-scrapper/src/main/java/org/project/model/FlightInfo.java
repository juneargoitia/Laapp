package org.project.model;

public class FlightInfo {
    private String flightNumber;
    private String origin;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private String flightStatus;
    private String capturedAt;

    public FlightInfo(
            String flightNumber, String origin, String destination,
            String departureTime, String arrivalTime, String flightStatus, String capturedAt
    ) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.flightStatus = flightStatus;
        this.capturedAt = capturedAt;
    }

    public String getFlightNumber() {
        return flightNumber;
    }
    public String getOrigin() {
        return origin;
    }
    public String getDestination() {
        return destination;
    }
    public String getDepartureTime() {
        return departureTime;
    }
    public String getArrivalTime() {
        return arrivalTime;
    }
    public String getFlightStatus() {
        return flightStatus;
    }
    public String getCapturedAt() {
        return capturedAt;
    }
}