package org.project.businessunit.footballdata.core;

import org.project.businessunit.footballdata.model.Match;
import java.util.List;

public interface FootballStore {
    void save(List<Match> matches);
}