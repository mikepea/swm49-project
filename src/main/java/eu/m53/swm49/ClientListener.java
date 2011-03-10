package eu.m53.swm49;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import com.google.gson.Gson;

public class ClientListener implements MessageListener {
    
    private ProcessState state = ProcessState.getInstance();
    private String job;

    public ClientListener(String job) {
        this.job = job;
    }

    public void onMessage(Message message) {
        try {
            System.out.println("ID " + state.getMyID() + ": Got message:");
            // we've received a message, pull out the text and work out what it is.
            Gson gson = new Gson();
            Task task = gson.fromJson(((TextMessage) message).getText(), Task.class);
            task.execute();
            
        } catch (Exception e) {
            System.out.println("ID " + state.getMyID() + ": Exception;");
            e.printStackTrace();
        }
    }

}
