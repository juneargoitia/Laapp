package org.project.footballdata.infrastructure;

import com.google.gson.Gson;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.project.footballdata.model.FootballEvent;
import org.project.footballdata.core.FootballStore;
import org.project.footballdata.model.Match;

import javax.jms.*;
import java.util.List;


public class FootballPublisher implements FootballStore {
    private final String brokerUrl;
    private final String topicName = "Football";
    private final Gson gson = new Gson();
    private final AirportMapper mapper = new AirportMapper();

    public FootballPublisher(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    @Override
    public void save(List<Match> matches) {
        Connection connection = null;
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
            connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(topicName);
            MessageProducer producer = session.createProducer(destination);

            for (Match m : matches) {
                String code = mapper.getCode(m.getLocalTeam(), m.getMatchday());
                m.setAirportCode(code);
                FootballEvent event = new FootballEvent(m);
                String json = gson.toJson(event);

                TextMessage message = session.createTextMessage(json);
                producer.send(message);
                System.out.println("Publicado en " + brokerUrl + ": " + json);
            }

        } catch (JMSException e) {
            System.err.println("Error enviando a Broker: " + e.getMessage());
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
