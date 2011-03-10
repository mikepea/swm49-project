/**
 * Task.java
 * 
 * Creates, and possibly executes a 'service {task} {method}' task,
 * for example to restart httpd.
 * 
 * setLockRequired is used to say that this task should only be
 * executed if a lock can be granted from the controller.
 * 
 */
package eu.m53.swm49;

import com.google.gson.Gson;

public class Task {
    private String name;
    private String method = "restart";
    private Boolean lock_required = false;
    private Boolean is_locked = false;
    private String results;
    private Integer lock_wait_count = 0;

    public Task() {};
    
    public Task( String name ) {
        this.name = name;
    };

    
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setLockRequired(Boolean bool) {
        if (bool) { this.setLock(); }
        this.lock_required = bool;
    }
    
    public Boolean getLockRequired() {
        return this.lock_required;
    }
    
    public Boolean isLocked() {
        return this.is_locked;
    }
    
    public void setLock() {
        this.is_locked = true;
    }
    
    public void releaseLock() {
        // do stuff, then:
        this.is_locked = false;
    }
    
    // Return the object as JSON, for passing over message queue
    public String getTaskAsJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    
    public void execute() {
        if (this.getLockRequired()) {
            if (this.isLocked()) {
                // can't execute this command
                System.out.println("Can't execute locked task: " + this.getName());
                System.out.println("Can't execute locked task: " + this.getName());
                // send message to controller queue requesting lock.
            } else {
                // lock has been released!
                System.out.println("Executing unlocked task: " + this.getName());
            }
            
        } else {
            System.out.println("Executing task: " + this.getName());
        }
    }
    
}
