package org.project.control;

import org.project.model.Match;
import java.util.List;

public class FootballController {
    private final FootballFeeder feeder;
    private final FootballStore store;

    // El controlador recibe "herramientas" genéricas, no clases fijas
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
