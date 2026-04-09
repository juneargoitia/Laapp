package org.project.core;

import org.project.model.Match;
import java.util.List;

public interface FootballFeeder {
    List<Match> getMatches() throws Exception;
}