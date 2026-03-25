package main.java.org.project;

public class Match {
    private int id;
    private String local_team;
    private String visitor_team;
    private String match_status;
    private int matchday;
    private String match_date;
    private String competition;
    private String captured_at;

    public Match(int id, String local_team, String visitor_team, String match_status,
                 int matchday, String match_date, String competition, String captured_at) {
        this.id = id;
        this.local_team = local_team;
        this.visitor_team = visitor_team;
        this.match_status = match_status;
        this.matchday = matchday;
        this.match_date = match_date;
        this.competition = competition;
        this.captured_at = captured_at;
    }

    public int getId() {
        return id;
    }

    public String getlocal_team() {
        return local_team;
    }

    public String getvisitor_team() {
        return visitor_team;
    }

    public String getmatch_status() {
        return match_status;
    }

    public int getMatchday() {
        return matchday;
    }

    public String getmatch_date() {
        return match_date;
    }

    public String getCompetition() {
        return competition;
    }

    public String getcaptured_at() {
        return captured_at;
    }
}
