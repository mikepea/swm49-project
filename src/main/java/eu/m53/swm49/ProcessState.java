package eu.m53.swm49;

import java.security.SecureRandom;

public class ProcessState {
    
    // Thread-safe if we eagerly create the instance:
    private static ProcessState uniqueInstance = new ProcessState();
    
    SecureRandom prng = new SecureRandom();
    
    private Integer myID = prng.nextInt();
    
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

}
