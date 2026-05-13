package org.project.travelscrapper.travelscrapper.infrastructure;

import com.google.gson.Gson;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.project.travelscrapper.travelscrapper.core.FlightStore;
import org.project.travelscrapper.travelscrapper.model.FlightEvent;
import org.project.travelscrapper.travelscrapper.model.FlightInfo;

import javax.jms.*;
import java.util.List;

public class FlightPublisher implements FlightStore {
    private final String brokerUrl;
    private static final String topicName = "Travel";
    private final Gson gson = new Gson();

    public FlightPublisher(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    @Override
    public void save(List<FlightInfo> flights) {
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
            }
                System.out.println("Vuelos enviados alBroker: " + brokerUrl);
        } catch (JMSException e) {
            System.err.println("Error en el Publisher de Viajes: " + e.getMessage());
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}


