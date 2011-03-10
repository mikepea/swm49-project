package eu.m53.swm49;

import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.JMSException;

public class ControllerProcess {

    private static ProcessConfig config = ProcessConfig.getInstance();
    
    private static String jobs[] = new String[]{"locks", "controller"};
    private static ProcessState state = ProcessState.getInstance();
    
    /**
     * @param args
     */
    public static void main(String[] args) throws JMSException {
        
        // start consuming messages from locks topic (lock requests)
        ClientConsumer consumer = new ClientConsumer();
        for (String job : jobs) {
            Destination destination = consumer.getSession().createTopic(config.getTopicBase() + "." + job);
            MessageConsumer messageConsumer = consumer.getSession().createConsumer(destination);
            if ( job.equals("controller") ) {
                messageConsumer.setMessageListener(new ControllerListener(job));
            } else if ( job.equals("locks") ) {
                messageConsumer.setMessageListener(new LockListener(job));
            } else {
                messageConsumer.setMessageListener(new ClientListener(job));
            }
        }
        
        while (true) {
            System.out.println("Controller ID: " + state.getMyID() + " - Loop!");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                System.out.println("Controller ID: " + state.getMyID() + " - Sleep interrupted!");
            }
        }
    }

}
