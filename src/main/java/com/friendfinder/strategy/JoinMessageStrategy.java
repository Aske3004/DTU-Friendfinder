package com.friendfinder.strategy;

import com.friendfinder.model.Message;
import org.springframework.stereotype.Component;

@Component
public class JoinMessageStrategy implements MessageTypeStrategy {

    @Override
    public void processMessage(Message message) {
        message.setContent(message.getSender().getUsername() + " joined the chat");
        System.out.println("User joined: " + message.getSender().getUsername());
    }

    @Override
    public String getMessageType() {
        return "JOIN";
    }
}
