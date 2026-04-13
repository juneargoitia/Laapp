package org.project.travelscrapper.infrastructure;

import com.google.gson.Gson;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.project.travelscrapper.model.FlightEvent;
import org.project.travelscrapper.model.FlightInfo;

import javax.jms.*;
import java.util.List;

public class FlightPublisher {
    private static final String brokerUrl = "tcp://localhost:61616";
    private static final String topicName = "Travel";
    private final Gson gson = new Gson();

    public void publish(List<FlightInfo> flights) {
        Connection connection = null;
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
            connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(topicName);
            MessageProducer producer = session.createProducer(destination);

            for (FlightInfo flight : flights) {
                FlightEvent event = new FlightEvent(flight);
                String json = gson.toJson(event);

                TextMessage message = session.createTextMessage(json);
                producer.send(message);

                System.out.println("Vuelo enviado a ActiveMQ: " + json);
            }

        } catch (JMSException e) {
            System.err.println("Error en el Publisher de Viajes: " + e.getMessage());
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (JMSException e) { e.printStackTrace(); }
        }
    }
}


