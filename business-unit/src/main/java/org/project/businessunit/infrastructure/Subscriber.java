package org.project.businessunit.infrastructure;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.project.businessunit.core.Datamart;
import org.project.businessunit.model.Flight;
import org.project.businessunit.model.Match;
import javax.jms.*;

public class Subscriber {
    private final String brokerUrl;
    private final Datamart datamart;

    public Subscriber(Datamart datamart, String brokerUrl) {
        this.datamart = datamart;
        this.brokerUrl = brokerUrl;
    }

    public void start() {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
            Connection connection = factory.createConnection();
            connection.setClientID("BusinessUnit-RT-" + System.currentTimeMillis());
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            setupConsumer(session, "Football");
            setupConsumer(session, "Travel");

            System.out.println(">>> Conectado al Broker: " + brokerUrl);
        } catch (JMSException e) {
            System.err.println(">>> ERROR: No se pudo conectar con " + brokerUrl);
        }
    }

    private void setupConsumer(Session session, String topicName) throws JMSException {
        MessageConsumer consumer = session.createConsumer(session.createTopic(topicName));
        consumer.setMessageListener(msg -> {
            try {
                if (msg instanceof TextMessage) {
                    process(JsonParser.parseString(((TextMessage) msg).getText()).getAsJsonObject(), topicName);
                }
            } catch (Exception e) {
                System.err.println("Error en topic " + topicName + ": " + e.getMessage());
            }
        });
    }

    private void process(JsonObject event, String topic) {
        if (topic.equals("Football")) {
            JsonObject m = event.getAsJsonObject("match");
            datamart.addMatch(new Match(
                    m.get("localTeam").getAsString(),
                    m.get("visitorTeam").getAsString(),
                    m.get("matchday").getAsString(),
                    m.get("matchDate").getAsString(),
                    m.get("matchStatus").getAsString(),
                    m.get("competition").getAsString(),
                    m.get("airportCode").getAsString().toUpperCase()
            ));
        } else {
            JsonObject f = event.getAsJsonObject("flight");
            datamart.addFlight(new Flight(
                    f.get("flightNumber").getAsString(),
                    f.get("airline").getAsString(),
                    f.get("origin").getAsString(),
                    f.get("destination").getAsString().toUpperCase(),
                    f.get("departureTime").getAsString(),
                    f.get("arrivalTime").getAsString(),
                    f.get("status").getAsString(),
                    f.get("capturedAt").getAsString()
            ));
        }
    }
}