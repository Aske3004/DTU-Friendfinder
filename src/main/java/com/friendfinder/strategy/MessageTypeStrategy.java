package com.friendfinder.strategy;

import com.friendfinder.model.Message;

public interface MessageTypeStrategy {
    void processMessage(Message message);
    String getMessageType();
}
