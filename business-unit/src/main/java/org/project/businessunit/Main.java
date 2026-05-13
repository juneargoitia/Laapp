package org.project.businessunit;

import org.project.businessunit.core.Datamart;
import org.project.businessunit.infrastructure.Subscriber;
import org.project.businessunit.infrastructure.RecordLoader;
import org.project.businessunit.presentation.View;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        String brokerUrl = (args.length > 0) ? args[0] :
                System.getenv().getOrDefault("BROKER_URL", "tcp://localhost:61616");

        Datamart datamart = new Datamart();

        RecordLoader loader = new RecordLoader(datamart);
        loader.loadAll("eventstore");

        Subscriber subscriber = new Subscriber(datamart, brokerUrl);
        subscriber.start();

        SwingUtilities.invokeLater(() -> {
            View view = new View(datamart);
            view.setVisible(true);
            System.out.println("=== INTERFAZ DE USUARIO INICIADA ===");
        });

        System.out.println("=== SISTEMA LISTO Y ACTUALIZADO ===");
    }
}