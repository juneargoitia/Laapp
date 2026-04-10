package org.project.travelscrapper.core;

import org.project.travelscrapper.model.FlightInfo;
import java.util.List;

public interface FlightStore {
    void save(List<FlightInfo> flights);
}