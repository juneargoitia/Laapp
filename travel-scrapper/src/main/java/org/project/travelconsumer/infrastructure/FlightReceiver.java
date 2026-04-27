package org.project.travelconsumer.infrastructure;

import com.google.gson.Gson;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.project.travelscrapper.model.FlightEvent;

import javax.jms.*;

public class FlightReceiver {
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "Travel";

    private final Gson gson = new Gson();
    private final EventStore eventStore = new EventStore();

    public void startListening() {
        try {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);

            Connection connection = factory.createConnection();
            connection.setClientID("TravelConsumer-StoreBuilder");
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Topic topic = session.createTopic(TOPIC_NAME);

            MessageConsumer consumer = session.createDurableSubscriber(topic, "TravelEventStore");

            System.out.println("-------------");
            System.out.println("    EVENT STORE BUILDER: ESCUCHANDO EVENTOS    ");
            System.out.println("  Topic: " + TOPIC_NAME + " | Broker: " + BROKER_URL);
            System.out.println("-------------");

            consumer.setMessageListener(message -> {
                try {
                    if (message instanceof TextMessage) {
                        String jsonText = ((TextMessage) message).getText();

                        FlightEvent event = gson.fromJson(jsonText, FlightEvent.class);

                        if (event != null) {
                            System.out.println(" - Evento recibido de " + event.getTs() + ". Almacenando...");

                            eventStore.store(TOPIC_NAME, "TravelConsumer", jsonText);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error procesando o guardando el evento: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            System.err.println("Error crítico en el Event Store Builder: " + e.getMessage());
        }
    }
}