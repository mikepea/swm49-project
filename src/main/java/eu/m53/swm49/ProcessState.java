package eu.m53.swm49;

import java.security.SecureRandom;

import org.apache.log4j.Logger;

public class ProcessState {
    
    private ProcessConfig config = ProcessConfig.getInstance();
    private Logger logger = Logger.getLogger(config.getLoggerBase());
    
    // Thread-safe if we eagerly create the instance:
    private static ProcessState uniqueInstance = new ProcessState();
        
    SecureRandom prng = new SecureRandom();
    
    private Integer myID = prng.nextInt(1000000000);
    private Boolean am_controller = false;
    private Task current_task;

    
    // other useful instance variables here
    
    private ProcessState() {}

    public static ProcessState getInstance() { 
        return uniqueInstance;
    }
    
    public Integer getMyID() {
        return myID;
    }
    
    public void setMyID() {
        myID = prng.nextInt();
    }
    
    public boolean amController() {
        return this.am_controller;
    }
    
    public void promoteToController() {
        System.out.println("ID: " + this.getMyID() + " - promoted to Controller!");
        logger.info("ID: " + this.getMyID() + " - promoted to Controller!");
        this.am_controller = true;
    }
    
    public Task currentTask() {
        return this.current_task;
    }
    
    public void setCurrentTask(Task task) {
        this.current_task = task;
    }
    
    public void clearCurrentTask() {
        this.current_task = null;
    }

}
