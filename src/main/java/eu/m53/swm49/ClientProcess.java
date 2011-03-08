package eu.m53.swm49;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;

public class ClientProcess {

    private static String jobs[] = new String[]{"suspend", "delete", "controller"};

	/**
	 * @param args
	 * @throws JMSException
	 */
    
    public static void main(String[] args) throws JMSException {
    	ClientConsumer consumer = new ClientConsumer();
    	for (String job : jobs) {
    		Destination destination = consumer.getSession().createQueue("swm49." + job);
    		MessageConsumer messageConsumer = consumer.getSession().createConsumer(destination);
    		if ( job.equals("controller") ) {
	    		messageConsumer.setMessageListener(new ControllerListener(job));
    		} else {
    		    messageConsumer.setMessageListener(new ClientListener(job));
    		}

    	}
    }

    
}
