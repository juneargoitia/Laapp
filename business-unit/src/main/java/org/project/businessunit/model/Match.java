package org.project.businessunit.model;

public record Match(
        String localTeam,
        String visitorTeam,
        String matchday,
        String matchStatus,
        String competition,
        String airportCode
) {}
