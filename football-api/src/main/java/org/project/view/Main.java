package org.project.view;

import org.project.client.FootballCliente;
import org.project.control.*;
import org.project.persistence.DatabaseManager;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        // Implementaciones
        FootballFeeder feeder = new FootballCliente();
        FootballStore store = new DatabaseManager();

        // Controlador
        FootballController controller = new FootballController(feeder, store);

        // Programador cada 1 hora
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            controller.run();
        }, 0, 1, TimeUnit.HOURS);
    }
}