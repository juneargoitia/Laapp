package org.project.businessunit.footballdata.core;

import org.project.businessunit.footballdata.model.Match;
import java.util.List;

public interface FootballFeeder {
    List<Match> getMatches() throws Exception;
}