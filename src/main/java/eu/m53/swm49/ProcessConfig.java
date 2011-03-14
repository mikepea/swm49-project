package eu.m53.swm49;

public class ProcessConfig {

    // Thread-safe if we eagerly create the instance:
    private static ProcessConfig uniqueInstance = new ProcessConfig();

    // other useful instance variables here
    private String jmsServer = "tcp://localhost:61616";

    private ProcessConfig() {}

    public static ProcessConfig getInstance() { 
        return uniqueInstance;
    }

    public String getJmsServer() {
        return jmsServer;
    }

    public void setJmsServer(String server) {
        this.jmsServer = server;
    }
    
    public String getLoggerBase() {
        return "swm49";
    }
    
    public String getQueueBase() {
        return "swm49";
    }
    
    public String getTopicBase() {
        return "swm49";
    }
    
    public Integer getMaxLockWaitCount() {
        return 5;
    }
    
    public Integer getMaxElectionWaitMillis() {
        return 5000;
    }

}
