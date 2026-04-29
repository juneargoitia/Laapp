package org.project.footballdata;

import org.project.footballdata.infrastructure.FootballClient;
import org.project.footballdata.core.FootballController;
import org.project.footballdata.core.FootballStore;
import org.project.footballdata.infrastructure.FootballPublisher;
import org.project.footballdata.core.FootballFeeder;


public class Main {
    public static void main(String[] args) {
        FootballFeeder feeder = new FootballClient();
        FootballStore store = new FootballPublisher();
        FootballController controller = new FootballController(feeder, store);
        controller.start();
    }
}