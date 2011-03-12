package eu.m53.swm49;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

public class LockListener implements MessageListener {
    
    private ProcessState state = ProcessState.getInstance();
    private ProcessConfig config = ProcessConfig.getInstance();
    private Logger logger = Logger.getLogger(config.getLoggerBase());
    
    private LockTable locktable = LockTable.getInstance();
    private String job;

    public LockListener(String job) {
        this.job = job;
    }

    public void onMessage(Message message) {
        try {
            //System.out.println("ID " + state.getMyID() + ": Got lock:");
            // we've received a lock request, pull out the text and work out what it is.
            Gson gson = new Gson();
            Lock lock = gson.fromJson(((TextMessage) message).getText(), Lock.class);
            
            //System.out.println("lock: " + lock.serializeAsJSON());
            
            if ( lock.getType().equals("granted") ) {
                processLockGranted(lock);
            } else if ( lock.getType().equals("unlocked") ) {
                processLockUnlocked(lock);
            } else if ( lock.getType().equals("denied") ) {
                processLockDenied(lock);
 
            } else {
                processLockRequest(lock);
            }
            
        } catch (Exception e) {
            System.out.println("ID " + state.getMyID() + ": Exception:");
            e.printStackTrace();
        }
    }
    
    public void processLockRequest(Lock lock) {
        if ( state.amController() ) {
            System.out.println("request lock: " + lock.serializeAsJSON());
            // work out if we can grant the lock
            if ( locktable.setLockBy(lock.getTask(), lock.getRequestingID()) ) {
                // sweet, lock granted
                this.sendLockGrantedMessage(lock);
            } else {
                // lock denied.
                this.sendLockDeniedMessage(lock);
            }
        } // if we're a Client, ignore lock requests.
    }
    
    public void processLockGranted(Lock lock) {
        if ( state.amController() ) {
            // ignore - am controller.
            // TODO: Controller should still be able to act as a client.
        } else {
            System.out.println("granted lock: " + lock.serializeAsJSON());

            // we're a client, check to see if the granted lock is for our task.
            // TODO: need to check that message came from our expected controller.
            //       once we have elections and the possibility of >1 controller.
            if ( lock.getRequestingID().equals(state.getMyID()) ) {
                // sweet, it's for us.
                System.out.println("ID " + state.getMyID() + ": Unlocking task! - " + lock.getTask());
                Task task = state.currentTask();
                task.releaseLock();
            } else {
                System.out.println("ID " + state.getMyID() + 
                        ": NOT Unlocking task! - it's for ID: " + lock.getRequestingID());
            }
        }
    }

    public void processLockDenied(Lock lock) {
        if ( state.amController() ) {
            // ignore - am controller.
            // TODO: Controller should still be able to act as a client.
        } else {
            // we're a client, check to see if the granted lock is for our task.
            // TODO: need to check that message came from our expected controller.
            //       once we have elections and the possibility of >1 controller.
            if ( lock.getRequestingID().equals(state.getMyID()) ) {
                // sweet, it's for us.
                System.out.println("ID " + state.getMyID() + ": Denied task! - " + lock.getTask());
                Task task = state.currentTask();
                task.resetLockWaitCount();
            }
        }
    }

    public void processLockUnlocked(Lock lock) {
        if ( state.amController() ) {
            // ignore - am controller.
            // TODO: Controller should still be able to act as a client.
            System.out.println("request unlock: " + lock.serializeAsJSON());
            // work out if we can unlock the lock
            if ( locktable.releaseLockBy(lock.getTask(), lock.getRequestingID()) ) {
                // sweet, lock released
                System.out.println("ID " + state.getMyID() + ": Lock released! - ");
            } else {
                // unlock failed.
                System.out.println("ID " + state.getMyID() + ": Lock release FAILED! - " + lock.serializeAsJSON());
            }
        } else {
            // Ignore - we're a client so have nothing to unlock.
        }
    }

    public void sendLockGrantedMessage(Lock lock) {
        // we are a copy of the original lock, so modify it and send it back.
        System.out.println("ID " + state.getMyID() + ": Granting Lock");
        lock.setType("granted");
        lock.setGrantingID(state.getMyID());
        try {
            lock.submit();
        } catch (JMSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    
    public void sendLockDeniedMessage(Lock lock) {
        // we are a copy of the original lock, so modify it and send it back.
        System.out.println("ID " + state.getMyID() + ": Denying Lock");
        lock.setType("denied");
        lock.setGrantingID(state.getMyID());
        try {
            lock.submit();
        } catch (JMSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

}