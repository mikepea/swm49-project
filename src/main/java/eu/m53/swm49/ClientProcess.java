package eu.m53.swm49;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;

public class ClientProcess {

    private static ProcessConfig config = ProcessConfig.getInstance();
    
    // task queue is provided by the 
    private static String jobs[] = new String[]{"tasks", "locks", "controller"};
    private static ProcessState state = ProcessState.getInstance();
    
    /**
    * @param args
    * @throws JMSException
    */
    
    public static void main(String[] args) throws JMSException {
        
        // start consuming messages from our task queue:
        ClientConsumer consumer = new ClientConsumer();
        for (String job : jobs) {
            Destination destination = consumer.getSession().createTopic(config.getTopicBase() + "." + job);
            MessageConsumer messageConsumer = consumer.getSession().createConsumer(destination);
            if ( job.equals("controller") ) {
                messageConsumer.setMessageListener(new ControllerListener(job));
            } else if ( job.equals("locks")) {
                messageConsumer.setMessageListener(new LockListener(job));
            } else {
                messageConsumer.setMessageListener(new ClientListener(job));
            }
        }
        while (true) {
            //System.out.println("ID: " + state.getMyID() + " - Loop!");
            Task task = state.currentTask();

            try {
                if ( task != null ) {
                    task.execute();
                }
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                System.out.println("ID: " + state.getMyID() + " - Sleep interrupted!");
            }
        }
    }

    
}
