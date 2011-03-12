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
        if ( state.amController() ) {
            return; // controller does not receive tasks yet - TODO
        }
        try {
            System.out.println("ID " + state.getMyID() + ": Got message:");
            // we've received a message, pull out the text and work out what it is.
            Gson gson = new Gson();
            Task task = gson.fromJson(((TextMessage) message).getText(), Task.class);
            if ( state.currentTask() != null ) {
                // we already have a task
                // TODO: need to add this new task to a queue?
                System.out.println("ID " + state.getMyID() + 
                        ": Received a task (" + 
                        task.getName() + 
                        ") but we already have one");

            } else {
                System.out.println("ID " + state.getMyID() + 
                        ": Received a task (" + 
                        task.getName() + 
                        ") - setting it as our current task!");
                state.setCurrentTask(task);
            }
            
        } catch (Exception e) {
            System.out.println("ID " + state.getMyID() + ": Exception;");
            e.printStackTrace();
        }
    }

}
