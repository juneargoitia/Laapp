package org.project.core;

import org.project.model.Match;
import java.util.List;

public interface FootballStore {
    void save(List<Match> matches);
}