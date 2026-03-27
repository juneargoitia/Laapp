package org.project.control;

import org.project.model.Match;
import java.util.List;

public interface FootballStore {
    void save(List<Match> matches);
}