package eu.m53.swm49;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import com.google.gson.Gson;

public class LockListener implements MessageListener {
    
    private ProcessState state = ProcessState.getInstance();
    private String job;

    public LockListener(String job) {
        this.job = job;
    }

    public void onMessage(Message message) {
        try {
            System.out.println("ID " + state.getMyID() + ": Got lock:");
            // we've received a lock request, pull out the text and work out what it is.
            Gson gson = new Gson();
            Lock lock = gson.fromJson(((TextMessage) message).getText(), Lock.class);
            
            
        } catch (Exception e) {
            System.out.println("ID " + state.getMyID() + ": Exception;");
            e.printStackTrace();
        }
    }

}