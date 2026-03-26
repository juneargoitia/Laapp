package org.project.model;

public class Match {
    private int id;
    private String localTeam;
    private String visitorTeam;
    private String matchStatus;
    private int matchday;
    private String matchDate;
    private String competition;
    private String capturedAt;

    public Match(int id, String localTeam, String visitorTeam, String matchStatus,
                 int matchday, String matchDate, String competition, String capturedAt) {
        this.id = id;
        this.localTeam = localTeam;
        this.visitorTeam = visitorTeam;
        this.matchStatus = matchStatus;
        this.matchday = matchday;
        this.matchDate = matchDate;
        this.competition = competition;
        this.capturedAt = capturedAt;
    }

    public int getId() {
        return id;
    }

    public String getLocalTeam() {
        return localTeam;
    }

    public String getVisitorTeam() {
        return visitorTeam;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public int getMatchday() {
        return matchday;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public String getCompetition() {
        return competition;
    }

    public String getCapturedAt() {
        return capturedAt;
    }
}
