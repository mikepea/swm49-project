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
        
        System.out.println("ID: " + state.getMyID() + " - START UP");

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
        
        // main event loop:
        while (true) {
            
            // sleep a moment to stop the CPU going craaazy when looping.
            waitABit(500);
            //System.out.println("ID: " + state.getMyID() + "... tick");
           
            if ( state.currentElection() != null ) {
                // we're mid-election.
                state.demoteToClient();
                processElection(state.currentElection());
            }
            
            //System.out.println("ID: " + state.getMyID() + " - Loop!");
            Task task = state.currentTask();
            if ( task == null ) {
                // no current task
                //System.out.println("ID: " + state.getMyID() + " - nothing to do.");
                continue;
            }
                        
            if ( task.getLockWaitCount() > config.getMaxLockWaitCount() ) {
                // something is up - we haven't heard from the controller in a while.
                // Drop the task (as we have a dead controller)
                // And force a re-election
                state.clearCurrentTask();
                sendElectionRequest();
            } else {
                // no election, so we're rolling...
                processCurrentTask(task);
            }

        }

        //System.out.println("ID: " + state.getMyID() + " - BROKEN OUT OF EVENT LOOP! THIS SHOULD NOT HAPPEN!");

    }
    
    private static void sendElectionRequest() {
        System.out.println("ID: " + state.getMyID() + " - sending election request!");
        Election election = new Election(state.getMyID());
        try {
            election.submit();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    
    private static void processElection(Election election) {
        //System.out.println("ID: " + state.getMyID() + " - in processElection");

        // so, we're in an election, what's happening?
        // if our request has not been replied to, then we're possibly the 
        if ( election.getTimeCreated() < System.currentTimeMillis() - config.getMaxElectionWaitMillis() ) {
            // then we've timed out somehow.
            if ( election.getType().equals("request") ) {
                // then we're the new controller, since we haven't received any election
                // messages from processes with a higher ID.
                state.promoteToController();
                election.setType("announcement");
                try {
                    election.submit();
                } catch (JMSException e) {
                    // TODO
                }
                state.clearCurrentElection();
            }
        }
                
    }
    

    private static void processCurrentTask(Task task) {
        if ( task != null ) {
            //System.out.println("ID: " + state.getMyID() + " - trying to execute!");
            task.execute();
        }

    }
    
    private static void waitABit(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            System.out.println("ID: " + state.getMyID() + " - Sleep interrupted!");
        }
    }
    
}
