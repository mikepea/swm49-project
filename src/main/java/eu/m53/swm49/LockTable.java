/*
 * LockTable.java
 * 
 * This is the list of granted locks on the controller.
 * 
 */

package eu.m53.swm49;

import java.util.concurrent.ConcurrentHashMap;

public class LockTable {

    // Thread-safe if we eagerly create the instance:
    private static LockTable uniqueInstance = new LockTable();

    private LockTable() {}

    private ConcurrentHashMap lockTable;
    
    public static LockTable getInstance() { 
        return uniqueInstance;
    }
    
    public boolean setLockBy(String task, Integer id) {
        if ( returnWhoLockedBy(task) == id) {
            return true; // we're just trying to set a lock that is already in place.
        } else if ( checkLocked(task) ) {
            return false; // already locked, by someone else
        } else {
            lockTable.put(task, id);
            return true;
        }
    }
    
    public boolean releaseLockBy(String task, Integer id) {
        if ( returnWhoLockedBy(task) == id) {
            lockTable.remove(task);
            return true;
        } else {
            return false;
        }
    }
    
    public Boolean checkLocked(String task) {
        if ( lockTable.containsKey(task)) {
            return true;
        } else {
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
