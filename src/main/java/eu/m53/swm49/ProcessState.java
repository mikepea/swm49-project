package eu.m53.swm49;

public class ProcessState {
    
    // Thread-safe if we eagerly create the instance:
    private static ProcessState uniqueInstance = new ProcessState();
    
    // other useful instance variables here
    
    private ProcessState() {}

    public static ProcessState getInstance() { 
        return uniqueInstance;
    }

}
