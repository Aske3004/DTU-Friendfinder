package com.friendfinder.strategy;

import com.friendfinder.model.Message;
import org.springframework.stereotype.Component;

@Component
public class LeaveMessageStrategy implements MessageTypeStrategy {

    @Override
    public void processMessage(Message message) {
        message.setContent(message.getSender().getUsername() + " left the chat");
        System.out.println("User left: " + message.getSender().getUsername());
    }

    @Override
    public String getMessageType() {
        return "LEAVE";
    }
}
