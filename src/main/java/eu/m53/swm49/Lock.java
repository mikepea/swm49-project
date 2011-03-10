/*
 * Lock.java
 * 
 * Format for a lock request:
 *  'i would like to ensure that no-one else handles task name 'bob' whilst I am handling it.'
 *  
 */
package eu.m53.swm49;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import com.google.gson.Gson;

public class Lock {
    private String task;
    private Integer requestingID;
    private Integer grantingID;
    private String type = "request"; // or 'granted'
    private static ProcessState state = ProcessState.getInstance();

    
    public Lock() {};
    
    public Lock(String task, Integer requestingID) {
        this.requestingID = requestingID;
        this.task = task;
    }
    
    public String getTask() {
        return this.task;
    }

    public void setTask(String task) {
        this.task = task;
    }
    
    public Integer getRequestingID() {
        return this.requestingID;
    }

    public void setRequestingID(Integer requestingID) {
        this.requestingID = requestingID;
    }
    
    public Integer getGrantingID() {
        return this.grantingID;
    }

    public void setGrantingID(Integer grantingID) {
        this.grantingID = grantingID;
    }
    
    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        if ( type.equals("request") ||
             type.equals("unlocked") ||
             type.equals("granted") ) {
            this.type = type;
        } else {
            System.out.println("URP! cannot setType!");
        }
    }
    
    // Return the object as JSON, for passing over message queue
    public String serializeAsJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    
    public void submit() throws JMSException {
        if (this.type.equals("request")) {
            System.out.println("ID: " + 
                    state.getMyID() + 
                    " - Submitting lock request for task: " 
                    + this.getTask());
        } else {
            System.out.println("ID: " + 
                    state.getMyID() + 
                    " - Sending lock grant for task: " + this.getTask() +
                    " to ID " + this.getRequestingID() );
        }
        
        ClientPublisher publisher = new ClientPublisher();
        publisher.sendTopicMessage("locks", this.serializeAsJSON());
        publisher.close();
    }

}
