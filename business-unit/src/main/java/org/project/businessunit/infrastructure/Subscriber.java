package org.project.businessunit.infrastructure;

import com.google.gson.Gson;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.project.businessunit.core.Datamart;
import javax.jms.*;
import java.util.List;
import java.util.Map;

public class Subscriber {
    private final String brokerUrl = "tcp://localhost:61616";
    private final Datamart datamart;
    private final Gson gson = new Gson();

    public Subscriber(Datamart datamart) {
        this.datamart = datamart;
    }

    public void start() {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
            Connection connection = factory.createConnection();
            connection.setClientID("BusinessUnit-RT"); // Suscriptor identificado
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            MessageConsumer footballConsumer = session.createConsumer(session.createTopic("Football"));
            footballConsumer.setMessageListener(msg -> processMessage(msg, "Football"));

            MessageConsumer travelConsumer = session.createConsumer(session.createTopic("Travel"));
            travelConsumer.setMessageListener(msg -> processMessage(msg, "Travel"));

            System.out.println(">>> Business Unit conectada a ActiveMQ. Esperando eventos...");

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void processMessage(Message message, String topic) {
        try {
            if (message instanceof TextMessage) {
                String json = ((TextMessage) message).getText();
                Map event = gson.fromJson(json, Map.class);

                if (topic.equals("Football")) {
                    Map match = (Map) event.get("match");
                    datamart.updateMatch(match);
                } else if (topic.equals("Travel")) {
                    Map flight = (Map) event.get("flight");
                    String dest = (String) flight.get("destination");
                    List<Map<String, Object>> list = datamart.getFlightsFor(dest);
                    list.add(flight);
                    datamart.updateFlights(dest, list);
                }
            }
        } catch (Exception e) {
            System.err.println("Error procesando mensaje de " + topic);
        }
    }
}