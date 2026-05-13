package org.project.travelscrapper.travelconsumer.infrastructure;

import com.google.gson.Gson;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class FlightReceiver {
    private final String brokerUrl;
    private static final String TRAVEL_TOPIC = "Travel";
    private static final String FOOTBALL_TOPIC = "Football";

    private final Gson gson = new Gson();
    private final EventStore eventStore = new EventStore();

    public FlightReceiver(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    public void startListening() {
        try {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);

            Connection connection = factory.createConnection();
            connection.setClientID("EventStore-GlobalBuilder");
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Topic travelTopic = session.createTopic(TRAVEL_TOPIC);
            MessageConsumer travelConsumer = session.createDurableSubscriber(travelTopic, "TravelStoreSub");
            travelConsumer.setMessageListener(message -> processAndStore(message, TRAVEL_TOPIC, "TravelConsumer"));

            Topic footballTopic = session.createTopic(FOOTBALL_TOPIC);
            MessageConsumer footballConsumer = session.createDurableSubscriber(footballTopic, "FootballStoreSub");
            footballConsumer.setMessageListener(message -> processAndStore(message, FOOTBALL_TOPIC, "FootballFeeder"));

            System.out.println("----------------------------------------------");
            System.out.println("    EVENT STORE BUILDER: ESCUCHANDO EVENTOS    ");
            System.out.println("  Topics: " + FOOTBALL_TOPIC + " y " + TRAVEL_TOPIC);
            System.out.println("  Broker: " + brokerUrl);
            System.out.println("----------------------------------------------");

        } catch (Exception e) {
            System.err.println("Error crítico en el Event Store Builder: " + e.getMessage());
        }
    }
    private void processAndStore(Message message, String topic, String source) {
        try {
            if (message instanceof TextMessage) {
                String jsonText = ((TextMessage) message).getText();

                if (jsonText != null && !jsonText.isEmpty()) {
                    System.out.println(" -> Evento recibido en [" + topic + "]. Guardando en Event Store...");
                    eventStore.store(topic, source, jsonText);
                }
            }
        } catch (Exception e) {
            System.err.println("Error procesando mensaje de " + topic + ": " + e.getMessage());
        }
    }
}