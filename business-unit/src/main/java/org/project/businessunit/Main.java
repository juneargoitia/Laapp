package org.project.businessunit;

import org.project.businessunit.core.Datamart;
import org.project.businessunit.infrastructure.Subscriber;
import org.project.businessunit.infrastructure.RecordLoader;
import org.project.businessunit.presentation.View;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Datamart datamart = new Datamart();

        System.out.println(">>> Cargando eventos históricos...");
        RecordLoader loader = new RecordLoader(datamart);
        loader.loadAll("eventstore");

        Subscriber subscriber = new Subscriber(datamart);
        subscriber.start();

        SwingUtilities.invokeLater(() -> {
            View view = new View(datamart);
            view.setVisible(true); // O view.display() si es por consola
            System.out.println("=== INTERFAZ DE USUARIO INICIADA ===");
        });

        System.out.println("=== SISTEMA LISTO Y ACTUALIZADO ===");
    }
}