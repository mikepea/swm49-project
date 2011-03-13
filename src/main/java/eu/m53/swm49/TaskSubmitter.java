package eu.m53.swm49;

import javax.jms.JMSException;

public class TaskSubmitter {

    /**
     * @param args
     */
    public static void main(String[] args) throws JMSException {
        
        boolean run_sequential = false;
        
        if ( args.length == 0 || args.length > 2 ) {
            System.err.println("Usage: TaskSubmitter TASK_NAME [SEQ]");
            System.exit(1);
        }
        
        String task_name = args[0];
        if ( args[1].equals("SEQ") ) {
            run_sequential = true;
        }
        
        if ( task_name == null ) {
            System.err.println("Usage: TaskSubmitter TASK_NAME [SEQ]");
            System.exit(1);
        }
        
        Task task = new Task(task_name);
        if ( run_sequential ) {
            task.setLockRequired(true);
        }
        
        ClientPublisher publisher = new ClientPublisher();
        publisher.sendTopicMessage("tasks", task.serializeAsJSON());
        publisher.close();

    }

}
