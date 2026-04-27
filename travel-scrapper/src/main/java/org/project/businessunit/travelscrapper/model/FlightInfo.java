package org.project.businessunit.travelscrapper.model;

public class FlightInfo {
    private final String flightNumber;
    private final String origin;
    private final String destination;
    private final String departureTime;
    private final String arrivalTime;
    private final String flightStatus;
    private final String airline;
    private final String capturedAt;

    public FlightInfo(
            String flightNumber, String origin, String destination, String departureTime,
            String arrivalTime, String flightStatus, String airline, String capturedAt
    ) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.flightStatus = flightStatus;
        this.airline = airline;
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
    public String getAirline() {
        return airline;
    }
    public String getCapturedAt() {
        return capturedAt;
    }
}