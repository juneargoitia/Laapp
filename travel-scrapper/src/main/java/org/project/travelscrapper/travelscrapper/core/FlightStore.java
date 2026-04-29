package org.project.travelscrapper.travelscrapper.core;

import org.project.travelscrapper.travelscrapper.model.FlightInfo;
import java.util.List;

public interface FlightStore {
    void save(List<FlightInfo> flights);
}