package org.project.businessunit.model;

public record Flight(
        String flightNumber,
        String airline,
        String origin,
        String destination,
        String departureTime,
        String arrivalTime,
        String status,
        String capturedAt,
        double price
) {}
