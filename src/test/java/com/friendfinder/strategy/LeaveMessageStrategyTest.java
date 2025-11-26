package com.friendfinder.strategy;

import com.friendfinder.model.Chat;
import com.friendfinder.model.Message;
import com.friendfinder.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LeaveMessageStrategyTest {

    private LeaveMessageStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new LeaveMessageStrategy();
    }

    @Test
    void getMessageType_returnsLEAVE() {
        assertEquals("LEAVE", strategy.getMessageType());
    }

    @Test
    void processMessage_setsLeaveContent() {
        Chat chat = new Chat("Test Chat");

        User user = new User();
        user.setName("Bob");
        user.setEmail("bob@gmail.com");
        user.setPassword("password");

        Message message = new Message(chat, user, "", "LEAVE");

        strategy.processMessage(message);

        assertTrue(message.getContent().contains("Bob"));
        assertTrue(message.getContent().contains("left the chat"));
    }
}
