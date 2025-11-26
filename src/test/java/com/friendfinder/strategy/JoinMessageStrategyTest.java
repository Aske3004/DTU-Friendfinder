package com.friendfinder.strategy;

import com.friendfinder.model.Chat;
import com.friendfinder.model.Message;
import com.friendfinder.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JoinMessageStrategyTest {

    private JoinMessageStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new JoinMessageStrategy();
    }

    @Test
    void getMessageType_returnsJOIN() {
        assertEquals("JOIN", strategy.getMessageType());
    }

    @Test
    void processMessage_setsJoinContent() {
        Chat chat = new Chat("Test Chat");

        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@gmail.com");
        user.setPassword("password");

        Message message = new Message(chat, user, "", "JOIN");

        strategy.processMessage(message);

        assertTrue(message.getContent().contains("Alice"));
        assertTrue(message.getContent().contains("joined the chat"));
    }
}
