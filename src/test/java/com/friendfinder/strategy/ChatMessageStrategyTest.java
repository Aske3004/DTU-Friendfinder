package com.friendfinder.strategy;

import com.friendfinder.model.Chat;
import com.friendfinder.model.Message;
import com.friendfinder.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChatMessageStrategyTest {

    private ChatMessageStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new ChatMessageStrategy();
    }

    @Test
    void getMessageType_returnsCHAT() {
        assertEquals("CHAT", strategy.getMessageType());
    }

    @Test
    void processMessage_doesNotModifyMessage() {
        Chat chat = new Chat("Test Chat");

        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@gmail.com");
        user.setPassword("alice");

        Message message = new Message(chat, user, "Hello!");

        String originalContent = message.getContent();
        strategy.processMessage(message);

        assertEquals(originalContent, message.getContent());

    }
}
