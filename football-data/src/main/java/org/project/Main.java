package org.project;

import org.project.infrastructure.FootballClient;
import org.project.core.FootballController;
import org.project.core.FootballStore;
import org.project.infrastructure.DatabaseManager;
import org.project.core.FootballFeeder;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        FootballFeeder feeder = new FootballClient();
        FootballStore store = new DatabaseManager();

        FootballController controller = new FootballController(feeder, store);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                controller.run();
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }, 0, 1, TimeUnit.HOURS);
    }
}