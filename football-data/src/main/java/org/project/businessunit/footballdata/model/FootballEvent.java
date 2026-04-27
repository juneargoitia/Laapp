package org.project.businessunit.footballdata.model;

public class FootballEvent {
    private final String ts;
    private final String ss;
    private final Match match;

    public FootballEvent(Match match) {
        this.ts = match.getCapturedAt();
        this.ss = "football-feeder";
        this.match = match;
    }

    public String getTs() { return ts; }
    public String getSs() { return ss; }
    public Match getMatch() { return match; }
}
