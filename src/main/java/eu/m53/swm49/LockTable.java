/*
 * LockTable.java
 * 
 * This is the list of granted locks on the controller.
 * 
 */

package eu.m53.swm49;

import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;

public class LockTable {

    private ProcessConfig config = ProcessConfig.getInstance();
    private Logger logger = Logger.getLogger(config.getLoggerBase());
    
    // Thread-safe if we eagerly create the instance:
    private static LockTable uniqueInstance = new LockTable();

    private LockTable() {}

    private ConcurrentHashMap<String, Integer> lockTable = new ConcurrentHashMap<String, Integer>();
    
    public static LockTable getInstance() { 
        return uniqueInstance;
    }
    
    public boolean setLockBy(String task, Integer id) {
        
        if ( this.returnWhoLockedBy(task) == id) {
            System.out.println("setLockBy: " + task + ", " + id + " - already locked by this ID.");
            return true; // we're just trying to set a lock that is already in place.
        } else if ( this.checkLocked(task) ) {
            System.out.println("setLockBy: " + task + ", " + id + " - already locked by another ID.");
            return false; // already locked, by someone else
        } else {
            // grant the lock
            System.out.println("setLockBy: " + task + ", " + id + " - was not locked - locking!");
            this.lockTable.put(task, id);
            return true;
        }
    }
    
    public boolean releaseLockBy(String task, Integer id) {
        if ( returnWhoLockedBy(task).equals(id) ) {
            lockTable.remove(task);
            return true;
        } else {
            return false;
        }
    }
    
    public Boolean checkLocked(String task) {
        if ( lockTable.containsKey(task)) {
            //System.out.println("task " + task + " - checkLocked locked!");
            return true;
        } else {
            //System.out.println("task " + task + " - checkLocked not locked");
            return false;
        }
    }
    
    public Integer returnWhoLockedBy(String task) {
        // return the ID of the process locking the task, or 0 if it's not locked.
        if (checkLocked(task)) {
            return (Integer) lockTable.get(task);
        } else {
            return 0;
        }
    }
}
