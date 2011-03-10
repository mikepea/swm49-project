/*
 * Lock.java
 * 
 * Format for a lock request:
 *  'i would like to ensure that no-one else handles task name 'bob' whilst I am handling it.'
 *  
 */
package eu.m53.swm49;

import javax.jms.TextMessage;

import com.google.gson.Gson;

public class Lock {
    private String task;
    private Integer requestingID;
    
    public Lock() {};
    
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
    
    // Return the object as JSON, for passing over message queue
    public String serializeAsJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
