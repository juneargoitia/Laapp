package org.project.footballdata;

import org.project.footballdata.infrastructure.FootballClient;
import org.project.footballdata.core.FootballController;
import org.project.footballdata.core.FootballStore;
import org.project.footballdata.infrastructure.FootballPublisher;
import org.project.footballdata.core.FootballFeeder;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        FootballFeeder feeder = new FootballClient();
        FootballStore store = new FootballPublisher();
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