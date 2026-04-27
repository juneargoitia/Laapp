package org.project.businessunit.footballdata;

import org.project.businessunit.footballdata.infrastructure.FootballClient;
import org.project.businessunit.footballdata.core.FootballController;
import org.project.businessunit.footballdata.core.FootballStore;
import org.project.businessunit.footballdata.infrastructure.FootballPublisher;
import org.project.businessunit.footballdata.core.FootballFeeder;


public class Main {
    public static void main(String[] args) {
        FootballFeeder feeder = new FootballClient();
        FootballStore store = new FootballPublisher();
        FootballController controller = new FootballController(feeder, store);
        controller.start();
    }
}