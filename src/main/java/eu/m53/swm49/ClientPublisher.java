package eu.m53.swm49;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

public class ClientPublisher {

    private static ProcessConfig config = ProcessConfig.getInstance();
    
    private static String brokerURL = config.getJmsServer();
    private static ConnectionFactory factory;
    private Connection connection;
    private Session session;
    private MessageProducer producer;
    
        
    public ClientPublisher() throws JMSException {
        factory = new ActiveMQConnectionFactory(brokerURL);
        connection = factory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        producer = session.createProducer(null);
    }    
    
    public void close() throws JMSException {
        if (connection != null) {
            connection.close();
        }
    }    
    
    public void sendQueueMessage(String job, String text) throws JMSException {
        Destination destination = session.createQueue(config.getQueueBase() + "." + job);
        Message message = session.createTextMessage(text);
        System.out.println("Sending message: " + ((TextMessage)message).getText() + " on queue: " + destination);
        producer.send(destination, message);
    }
    
    public void sendTopicMessage(String job, String text) throws JMSException {
        Destination destination = session.createTopic(config.getTopicBase() + "." + job);
        Message message = session.createTextMessage(text);
        System.out.println("Sending message: " + ((TextMessage)message).getText() + " on topic: " + destination);
        producer.send(destination, message);
    }

}

