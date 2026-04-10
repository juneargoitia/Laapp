package org.project.footballdata.core;

import org.project.footballdata.model.Match;
import java.util.List;

public class FootballController {
    private final FootballFeeder feeder;
    private final FootballStore store;

    public FootballController(FootballFeeder feeder, FootballStore store) {
        this.feeder = feeder;
        this.store = store;
    }

    public void run() {
        try {
            System.out.println("Iniciando...");
            List<Match> matches = feeder.getMatches();
            store.save(matches);
            System.out.println("Ok: " + matches.size() + " partidos procesados.");
        } catch (Exception e) {
            System.err.println("Error en la ejecución: " + e.getMessage());
        }
    }
}