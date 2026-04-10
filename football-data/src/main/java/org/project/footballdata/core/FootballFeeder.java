package org.project.footballdata.core;

import org.project.footballdata.model.Match;
import java.util.List;

public interface FootballFeeder {
    List<Match> getMatches() throws Exception;
}