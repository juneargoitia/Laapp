package org.project.persistence;

import org.project.control.FlightStore;
import org.project.model.FlightInfo;
import java.util.List;

public class FlightDatabase implements FlightStore {
    @Override
    public void save(List<FlightInfo> flights) {
        System.out.println(("Simulando guardado de vuelos en SQLite..."));
    }
}
