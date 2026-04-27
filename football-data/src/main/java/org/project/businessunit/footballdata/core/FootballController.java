package org.project.businessunit.footballdata.core;

import org.project.businessunit.footballdata.model.Match;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FootballController {
    private final FootballFeeder feeder;
    private final FootballStore store;

    public FootballController(FootballFeeder feeder, FootballStore store) {
        this.feeder = feeder;
        this.store = store;
    }

    public void start() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                execute();
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }, 0, 1, TimeUnit.HOURS);
    }

    private void execute() {
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