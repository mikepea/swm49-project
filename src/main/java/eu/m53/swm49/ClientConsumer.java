package eu.m53.swm49;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

public class ClientConsumer {

    private static ProcessConfig config = ProcessConfig.getInstance();

    private static String brokerURL = config.getJmsServer();
    private static ConnectionFactory factory;
    private Connection connection;
    private Session session;
    public ClientConsumer() throws JMSException {
        factory = new ActiveMQConnectionFactory(brokerURL);
        connection = factory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    public void close() throws JMSException {
        if (connection != null) {
            connection.close();
        }
    }    

    public Session getSession() {
        return session;
    }

}
