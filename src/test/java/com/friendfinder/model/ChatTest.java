package com.friendfinder.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashSet;
import java.util.Set;
import static org.mockito.Mockito.*;

public class ChatTest {

    @Test
    void defaultConstructor_initializesCreatedAtAndCollections() {
        Chat chat = new Chat();

        assertNotNull(chat.getCreatedAt());
        assertNotNull(chat.getParticipants());
        assertNotNull(chat.getMessages());
        assertTrue(chat.getParticipants().isEmpty());
        assertTrue(chat.getMessages().isEmpty());
        assertNull(chat.getChatType());
    }

    @Test
    void nameConstructor_setsNameAndDefaultGroupType() {
        Chat chat = new Chat("Friends");

        assertEquals("Friends", chat.getChatName());
        assertNotNull(chat.getCreatedAt());
        assertEquals("GROUP", chat.getChatType());
    }

    @Test
    void fullConstructor_setsNameCreatedAtAndType() {
        Chat chat = new Chat("Project Chat", "PRIVATE");

        assertEquals("Project Chat", chat.getChatName());
        assertNotNull(chat.getCreatedAt());
        assertEquals("PRIVATE", chat.getChatType());
    }

    @Test
    void addMessage_addsMessageAndSetsBackReference() {
        Chat chat = new Chat("Friends");
        Message message = mock(Message.class);

        chat.addMessage(message);

        assertEquals(1, chat.getMessages().size());
        verify(message).setChat(chat);
    }

    @Test
    void removeMessage_removesMessageAndClearsBackReference() {
        Chat chat = new Chat("Friend");
        Message message = mock(Message.class);

        chat.addMessage(message);
        chat.removeMessage(message);

        assertTrue(chat.getMessages().isEmpty());
        verify(message).setChat(null);
    }

    @Test
    void addParticipant_updatesBothChatAndUser() {
        Chat chat = new Chat("Friends");

        User user = mock(User.class);
        Set<Chat> userChats = new HashSet<>();
        when(user.getChats()).thenReturn(userChats);

        chat.addParticipant(user);

        assertTrue(chat.getParticipants().contains(user));
        assertTrue(userChats.contains(chat));
    }

    @Test
    void removeParticipant_updatesBothChatAndUser() {
        Chat chat = new Chat("Friends");

        User user = mock(User.class);
        Set<Chat> userChats = new HashSet<>();
        when(user.getChats()).thenReturn(userChats);

        chat.addParticipant(user);
        chat.removeParticipant(user);

        assertFalse(chat.getParticipants().contains(user));
        assertFalse(userChats.contains(chat));
    }

    @Test
    void equalsAndHashCode_basedOnChatIdOnly() {
        Chat chat1 = new Chat("A");
        Chat chat2 = new Chat("A");
        Chat chat3 = new Chat("B");

        chat1.setChatId(1L);
        chat2.setChatId(1L);
        chat3.setChatId(2L);

        assertEquals(chat1, chat2);
        assertNotEquals(chat1, chat3);
    }

    @Test
    void toString_containsBasicInfo() {
        Chat chat = new Chat("Friends");
        chat.setChatId(42L);

        String str = chat.toString();

        assertTrue(str.contains("Friends"));
        assertTrue(str.contains("42"));
    }
}
