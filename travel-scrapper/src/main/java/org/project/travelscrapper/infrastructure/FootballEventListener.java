package org.project.travelscrapper.infrastructure;

import com.google.gson.Gson;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.project.travelscrapper.core.FlightController;
import javax.jms.*;

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
                        // Extraemos el código del aeropuerto del JSON del partido
                        // Nota: Usamos un mapa simple para no tener que importar el modelo de fútbol
                        java.util.Map event = gson.fromJson(json, java.util.Map.class);
                        java.util.Map match = (java.util.Map) event.get("match");
                        String airportCode = (String) match.get("airportCode");

                        if (airportCode != null && !airportCode.equals("N/A") && !airportCode.isEmpty()) {
                            System.out.println("\n[EVENTO RECIBIDO] Nuevo partido detectado. Destino: " + airportCode);
                            // Llamamos al controlador para que ejecute el scraping HACIA ese destino
                            controller.executeWithDestination(airportCode);
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
