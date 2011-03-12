package eu.m53.swm49;

import javax.jms.JMSException;
import com.google.gson.Gson;

public class Election {
    
    private Integer requestingID;
    private String type = "request"; // or 'announcement'
    private static ProcessState state = ProcessState.getInstance();
    private static ProcessConfig config = ProcessConfig.getInstance();
    
    private long time_created;
    
    private Election() {};

    public Election(Integer requestingID) {
        this.requestingID = requestingID;
        this.time_created = System.currentTimeMillis();
    }

    public Election(String type, Integer requestingID) {
        this.requestingID = requestingID;
        this.type = type;
        this.time_created = System.currentTimeMillis();
    }
    
    public long getTimeCreated() {
        return this.time_created;
    }
    
    public Integer getRequestingID() {
        return this.requestingID;
    }

    public void setRequestingID(Integer requestingID) {
        this.requestingID = requestingID;
    }
 
    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        if ( type.equals("request") ||
             type.equals("announcement") ) {
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
                    " - Submitting election request from ID: " 
                    + this.getRequestingID());
            state.setCurrentElection(this);
        } else {
            System.out.println("ID: " + 
                    state.getMyID() + 
                    " - Sending election announcement that I am the boss!");
        }
        
        ClientPublisher publisher = new ClientPublisher();
        publisher.sendTopicMessage("controller", this.serializeAsJSON());
        publisher.close();
    }
       

}
