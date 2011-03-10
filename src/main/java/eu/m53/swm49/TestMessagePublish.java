/**
 * 
 */
package eu.m53.swm49;

import javax.jms.JMSException;

/**
 * @author mike
 *
 */
public class TestMessagePublish {
    public static void main(String[] args) throws JMSException {
        
        Task task1 = new Task("bob");
        ClientPublisher publisher = new ClientPublisher();
        publisher.sendTopicMessage("tasks", task1.getTaskAsJSON());
        publisher.close();
    }
}
