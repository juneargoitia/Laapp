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
    private int scoreHome;
    private int scoreAway;

    public Match(int id, String localTeam, String visitorTeam,
                 String matchStatus, int matchday, String matchDate,
                 String competition, String capturedAt,
                 int scoreHome, int scoreAway) {
        this.id = id;
        this.localTeam = localTeam;
        this.visitorTeam = visitorTeam;
        this.matchStatus = matchStatus;
        this.matchday = matchday;
        this.matchDate = matchDate;
        this.competition = competition;
        this.capturedAt = capturedAt;
        this.scoreHome = scoreHome;
        this.scoreAway = scoreAway;
    }

    public int getId() { return id; }
    public String getLocalTeam() { return localTeam; }
    public String getVisitorTeam() { return visitorTeam; }
    public String getMatchStatus() { return matchStatus; }
    public int getMatchday() { return matchday; }
    public String getMatchDate() { return matchDate; }
    public String getCompetition() { return competition; }
    public String getCapturedAt() { return capturedAt; }
    public int getScoreHome() { return scoreHome; }
    public int getScoreAway() { return scoreAway; }
}