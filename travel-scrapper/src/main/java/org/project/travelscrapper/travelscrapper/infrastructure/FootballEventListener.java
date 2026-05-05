package org.project.travelscrapper.travelscrapper.infrastructure;

import com.google.gson.Gson;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.project.travelscrapper.travelscrapper.core.FlightController;
import javax.jms.*;
import java.time.LocalDate;

public class FootballEventListener {
    private final String brokerUrl = "tcp://localhost:61616";
    private final String topicName = "Football";
    private final FlightController controller;
    private final Gson gson = new Gson();

    public FootballEventListener(FlightController controller) {
        this.controller = controller;
    }

    public void start() {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(topicName);
            MessageConsumer consumer = session.createConsumer(topic);

            System.out.println(">>> Travel-Scrapper escuchando partidos en Topic: Football...");

            consumer.setMessageListener(message -> {
                try {
                    if (message instanceof TextMessage) {
                        String json = ((TextMessage) message).getText();

                        java.util.Map event = gson.fromJson(json, java.util.Map.class);
                        java.util.Map match = (java.util.Map) event.get("match");

                        String airportCode = (String) match.get("airportCode");

                        String matchDateStr = (String) match.get("matchday");

                        if (airportCode != null && !airportCode.equals("N/A") && !airportCode.isEmpty()) {
                            System.out.println("\n[EVENTO RECIBIDO] Partido detectado. Destino: "
                                    + airportCode + " | Fecha: " + matchDateStr);

                            if (matchDateStr != null && !matchDateStr.isEmpty()) {
                                LocalDate matchDate = LocalDate.parse(matchDateStr.substring(0, 10));
                                controller.executeWithMatch(airportCode, matchDate);
                            } else {
                                System.out.println("Advertencia: el evento no incluye fecha del partido. Buscando vuelos para hoy.");
                                controller.executeWithDestination(airportCode);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error procesando evento de fútbol: " + e.getMessage());
                }
            });

        } catch (JMSException e) {
            System.err.println("Error en el Listener de Fútbol: " + e.getMessage());
        }
    }
}