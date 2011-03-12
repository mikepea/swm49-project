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
import javax.jms.JMSException;

public class Task {
    
    private static ProcessState state = ProcessState.getInstance();

    private String name;
    //private String method = "restart";
    private Boolean lock_required = false;
    private Boolean is_locked = false;
    //private String results;
    
    // counter for non-receipt of a lock ack message, to detect controller failure.
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
    
    public Integer getLockWaitCount() {
        return this.lock_wait_count;
    }
    
    public void incLockWaitCount() {
        this.lock_wait_count++;
    }
    
    public void resetLockWaitCount() {
        this.lock_wait_count = 0;
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
    
    // Return the object as JSON, for passing over message queue
    public String getTaskAsJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    
    private void actuallyExecute() {
        // private, since we need to wrap locking logic around if needed.
        // TODO: actually execute something!
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void execute() {
        this.incLockWaitCount();
        if (this.getLockRequired()) {
            if (this.isLocked()) {
                // can't execute this command try to get a lock.
                System.out.println("Can't execute locked task: " + this.getName());
                this.requestLock();
            } else {
                // lock has been released!
                System.out.println("Executing non-concurrent task: " + this.getName());
                this.actuallyExecute();
                this.releaseLock();
                state.clearCurrentTask();
            }
            
        } else {
            System.out.println("Executing concurrent task: " + this.getName());
            this.actuallyExecute();
            state.clearCurrentTask();
        }
    }
    
    public void requestLock() {
        Lock lock = new Lock(this.name, state.getMyID());
        try {
            lock.submit();
        } catch (JMSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void releaseLock() {
        // TODO do stuff, then:
        Lock lock = new Lock(this.name, state.getMyID());
        lock.setType("unlocked");
        try {
            lock.submit();
        } catch (JMSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.is_locked = false;
    }
    
}
