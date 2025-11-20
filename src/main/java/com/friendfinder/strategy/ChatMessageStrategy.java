package com.friendfinder.strategy;

import com.friendfinder.model.Message;
import com.friendfinder.repository.MessageRepository;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageStrategy implements MessageTypeStrategy {

    @Override
    public void processMessage(Message message) {
        System.out.println("Processing chat message: " + message.getContent());
    }

    @Override
    public String getMessageType() {
        return "CHAT";
    }
}
