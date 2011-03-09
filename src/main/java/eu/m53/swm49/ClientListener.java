package eu.m53.swm49;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

public class ClientListener  implements MessageListener {
    
    private String job;

    public ClientListener(String job) {
        this.job = job;
    }

    public void onMessage(Message message) {
        try {
            //do something here
            System.out.println(job + " id:" + ((ObjectMessage)message).getObject());
            //System.out.println(job + ": Got message: " + message.getJMSType());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
