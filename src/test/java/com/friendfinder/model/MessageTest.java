package com.friendfinder.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MessageTest {

    @Test
    void defaultConstructor_initializesDefaults() {
        Message message = new Message();

        assertNotNull(message.getTimestamp(), "timestamp should be initialized");
        assertEquals("CHAT", message.getMessageType(), "default messageType should be CHAT");
        assertFalse(message.getIsEdited(), "isEdited should be false by default");
        assertNotNull(message.getReadReceipts(), "readReceipts should not be null");
        assertTrue(message.getReadReceipts().isEmpty(), "readReceipts should start empty");
        assertNull(message.getChat());
        assertNull(message.getSender());
        assertNull(message.getContent());
    }

    @Test
    void essentialConstructor_setsChatSenderContentAndDefaults() {
        Chat chat = mock(Chat.class);
        User sender = mock(User.class);

        Message message = new Message(chat, sender, "Hello");

        assertEquals(chat, message.getChat());
        assertEquals(sender, message.getSender());
        assertEquals("Hello", message.getContent());
        assertNotNull(message.getTimestamp());
        assertEquals("CHAT", message.getMessageType());
        assertFalse(message.getIsEdited());
        assertNotNull(message.getReadReceipts());
        assertTrue(message.getReadReceipts().isEmpty());
    }

    @Test
    void typeConstructor_setsCustomType() {
        Chat chat = mock(Chat.class);
        User sender = mock(User.class);

        Message message = new Message(chat, sender, "System notice", "SYSTEM");

        assertEquals(chat, message.getChat());
        assertEquals(sender, message.getSender());
        assertEquals("System notice", message.getContent());
        assertNotNull(message.getTimestamp());
        assertEquals("SYSTEM", message.getMessageType());
        assertFalse(message.getIsEdited());
    }

    @Test
    void settersAndGetters_workAsExpected() {
        Message message = new Message();
        Chat chat = mock(Chat.class);
        User sender = mock(User.class);
        LocalDateTime customTimestamp = LocalDateTime.now().minusMinutes(5);
        LocalDateTime editedAt = LocalDateTime.now().minusMinutes(1);

        message.setMessageId(10L);
        message.setChat(chat);
        message.setSender(sender);
        message.setContent("Test content");
        message.setTimestamp(customTimestamp);
        message.setMessageType("JOIN");
        message.setIsEdited(true);
        message.setEditedAt(editedAt);
        var receipts = new ArrayList<MessageReadReceipt>();
        message.setReadReceipts(receipts);

        assertEquals(10L, message.getMessageId());
        assertEquals(chat, message.getChat());
        assertEquals(sender, message.getSender());
        assertEquals("Test content", message.getContent());
        assertEquals(customTimestamp, message.getTimestamp());
        assertEquals("JOIN", message.getMessageType());
        assertTrue(message.getIsEdited());
        assertEquals(editedAt, message.getEditedAt());
        assertSame(receipts, message.getReadReceipts());
    }

    @Test
    void markAsEdited_setsIsEditedAndEditedAt() {
        Message message = new Message();
        assertFalse(message.getIsEdited());
        assertNull(message.getEditedAt());

        message.markAsEdited();

        assertTrue(message.getIsEdited(), "isEdited should be true after markAsEdited()");
        assertNotNull(message.getEditedAt(), "editedAt should be set after markAsEdited()");
    }

    @Test
    void addReadReceipt_addsReceiptAndSetsBackReference() {
        Message message = new Message();
        MessageReadReceipt receipt = mock(MessageReadReceipt.class);

        message.addReadReceipt(receipt);

        assertEquals(1, message.getReadReceipts().size());
        assertTrue(message.getReadReceipts().contains(receipt));
        // back reference
        verify(receipt).setMessage(message);
    }

    @Test
    void hasUserRead_returnsTrueIfReceiptExistsForUser() {
        Message message = new Message();

        User user = mock(User.class);
        User otherUser = mock(User.class);

        MessageReadReceipt receipt = mock(MessageReadReceipt.class);
        when(receipt.getUser()).thenReturn(user);

        message.addReadReceipt(receipt);

        assertTrue(message.hasUserRead(user), "Expected user to have read the message");
        assertFalse(message.hasUserRead(otherUser), "Expected otherUser NOT to have read the message");
    }

    @Test
    void getReadCount_returnsNumberOfReceipts() {
        Message message = new Message();

        MessageReadReceipt r1 = mock(MessageReadReceipt.class);
        MessageReadReceipt r2 = mock(MessageReadReceipt.class);

        message.addReadReceipt(r1);
        message.addReadReceipt(r2);

        assertEquals(2, message.getReadCount());
    }

    @Test
    void isSystemMessage_returnsTrueForJoinLeaveSystem() {
        Message joinMessage = new Message();
        joinMessage.setMessageType("JOIN");
        assertTrue(joinMessage.isSystemMessage());

        Message leaveMessage = new Message();
        leaveMessage.setMessageType("LEAVE");
        assertTrue(leaveMessage.isSystemMessage());

        Message systemMessage = new Message();
        systemMessage.setMessageType("SYSTEM");
        assertTrue(systemMessage.isSystemMessage());
    }

    @Test
    void isSystemMessage_returnsFalseForNormalChat() {
        Message normalMessage = new Message();
        normalMessage.setMessageType("CHAT");
        assertFalse(normalMessage.isSystemMessage());
    }

    @Test
    void equalsAndHashCode_basedOnMessageIdOnly() {
        Message m1 = new Message();
        Message m2 = new Message();
        Message m3 = new Message();

        m1.setMessageId(1L);
        m2.setMessageId(1L);
        m3.setMessageId(2L);

        assertEquals(m1, m2);
        assertEquals(m1.hashCode(), m2.hashCode());
        assertNotEquals(m1, m3);
        assertNotEquals(m2, m3);

        Message noId1 = new Message();
        Message noId2 = new Message();
        assertNotEquals(noId1, noId2);
        assertEquals(noId1, noId1);
    }

    @Test
    void toString_containsKeyInformation() {
        Message message = new Message();
        message.setMessageId(5L);
        message.setContent("Hello world");
        message.setMessageType("CHAT");

        Chat chat = mock(Chat.class);
        User sender = mock(User.class);
        when(chat.getChatId()).thenReturn(99L);
        when(sender.getUserId()).thenReturn(77L);
        message.setChat(chat);
        message.setSender(sender);

        String str = message.toString();

        assertTrue(str.contains("5"));
        assertTrue(str.contains("Hello world"));
        assertTrue(str.contains("CHAT"));
        assertTrue(str.contains("99"));
        assertTrue(str.contains("77"));
    }
}
