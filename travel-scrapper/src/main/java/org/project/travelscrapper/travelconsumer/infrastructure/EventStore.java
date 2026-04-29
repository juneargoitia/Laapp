package org.project.travelscrapper.travelconsumer.infrastructure;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EventStore {
    private final String BASE_PATH = "eventstore";

    public void store(String topic, String source, String jsonMessage) {
        try {
            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            File directory = new File(BASE_PATH + File.separator + topic + File.separator + source);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, date + ".events");

            try (FileWriter fw = new FileWriter(file, true);
                 PrintWriter pw = new PrintWriter(fw)) {
                pw.println(jsonMessage);
            }

        } catch (Exception e) {
            System.err.println("Error guardando en Event Store: " + e.getMessage());
        }
    }
}
