package com.friendfinder.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MessageReadReceiptTest {

    @Test
    void defaultConstructor_initializesFieldsToNull() {
        MessageReadReceipt receipt = new MessageReadReceipt();

        assertNull(receipt.getId());
        assertNull(receipt.getMessage());
        assertNull(receipt.getUser());
        assertNull(receipt.getReadAt());
    }

    @Test
    void fullConstructor_setsAllFields() {
        Message message = mock(Message.class);
        User user = mock(User.class);
        LocalDateTime readAt = LocalDateTime.now();

        MessageReadReceipt receipt = new MessageReadReceipt(message, user, readAt);

        assertNull(receipt.getId()); // id sættes først af JPA
        assertEquals(message, receipt.getMessage());
        assertEquals(user, receipt.getUser());
        assertEquals(readAt, receipt.getReadAt());
    }

    @Test
    void settersAndGetters_workAsExpected() {
        MessageReadReceipt receipt = new MessageReadReceipt();

        Message message = mock(Message.class);
        User user = mock(User.class);
        LocalDateTime readAt = LocalDateTime.now().minusMinutes(5);

        receipt.setId(10L);
        receipt.setMessage(message);
        receipt.setUser(user);
        receipt.setReadAt(readAt);

        assertEquals(10L, receipt.getId());
        assertEquals(message, receipt.getMessage());
        assertEquals(user, receipt.getUser());
        assertEquals(readAt, receipt.getReadAt());
    }

    @Test
    void equalsAndHashCode_basedOnIdOnly() {
        MessageReadReceipt r1 = new MessageReadReceipt();
        MessageReadReceipt r2 = new MessageReadReceipt();
        MessageReadReceipt r3 = new MessageReadReceipt();

        r1.setId(1L);
        r2.setId(1L);
        r3.setId(2L);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
        assertNotEquals(r2, r3);

        MessageReadReceipt noId1 = new MessageReadReceipt();
        MessageReadReceipt noId2 = new MessageReadReceipt();
        assertEquals(noId1, noId2);
        assertEquals(noId1, noId1);
    }

    @Test
    void toString_containsKeyInformation() {
        MessageReadReceipt receipt = new MessageReadReceipt();

        receipt.setId(5L);
        LocalDateTime readAt = LocalDateTime.now();
        receipt.setReadAt(readAt);

        Message message = mock(Message.class);
        User user = mock(User.class);

        when(message.getMessageId()).thenReturn(99L);
        when(user.getUserId()).thenReturn(77L);

        receipt.setMessage(message);
        receipt.setUser(user);

        String str = receipt.toString();

        assertTrue(str.contains("5"));
        assertTrue(str.contains("99"));
        assertTrue(str.contains("77"));
        assertTrue(str.contains(readAt.toString()));
    }
}

