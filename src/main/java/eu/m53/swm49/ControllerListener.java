package eu.m53.swm49;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

public class ControllerListener implements MessageListener {

    private ProcessState state = ProcessState.getInstance();
    private ProcessConfig config = ProcessConfig.getInstance();
    private Logger logger = Logger.getLogger(config.getLoggerBase());
    
    private String job;

    public ControllerListener(String job) {
        this.job = job;
    }

    public void onMessage(Message message) {
        try {
            //System.out.println("ID " + state.getMyID() + ": Got lock:");
            // we've received a lock request, pull out the text and work out what it is.
            Gson gson = new Gson();
            Election election = gson.fromJson(((TextMessage) message).getText(), Election.class);
            
            //System.out.println("ID: " + state.getMyID() + " - got election message: " + election.serializeAsJSON());
            
            if ( election.getType().equals("announcement") ) {
                // this is hopefully the new controller.
                // but we should check that its ID is higher than ours,
                // otherwise force a re-election.
                if ( election.getRequestingID() < state.getMyID() ) {
                    Election new_election = new Election(state.getMyID());
                    try {
                        new_election.submit();
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                } else if ( election.getRequestingID().equals(state.getMyID()) ) {
                    // echo'd request. Ignore
                } else {
                    state.demoteToClient();
                    state.clearCurrentElection();
                }
            } else {
                // a request to be the controller
                // if ID is higher than mine, i am not the new controller.
                // if ID is lower than mine, then also request an election.
                if ( election.getRequestingID() < state.getMyID() ) {
                    // lower ID wants to be elected, no way!
                    if ( state.currentElection() == null ) {
                        // i need to request election for myself.
                        Election new_election = new Election(state.getMyID());
                        try {
                            new_election.submit();
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    }
                } else if ( election.getRequestingID().equals(state.getMyID()) ) {
                    // echo'd request. Ignore
                } else {
                    // i am not the new controller. Ditch any election request i have made.
                    state.demoteToClient();
                    state.clearCurrentElection();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
