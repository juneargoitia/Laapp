package org.project.travelscrapper.travelscrapper.infrastructure;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.project.travelscrapper.travelscrapper.core.FlightController;
import javax.jms.*;

public class FootballEventListener {
    private final String brokerUrl = "tcp://localhost:61616";
    private final String topicName = "Football";
    private final FlightController controller;

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
                        controller.processFootballEvent(json);
                    }
                } catch (Exception e) {
                    System.err.println("Error recibiendo mensaje de fútbol: " + e.getMessage());
                }
            });

        } catch (JMSException e) {
            System.err.println("Error en el Listener de Fútbol: " + e.getMessage());
        }
    }
}