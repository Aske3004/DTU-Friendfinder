package com.friendfinder.factory;

import com.friendfinder.model.Chat;
import com.friendfinder.model.User;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ChatFactoryTest {

    private final ChatFactory factory = new ChatFactory();

    @Test
    void createDirectChat_createsAlphabeticalNameAndAddsTwoParticipants() {
        // Arrange
        User u1 = mock(User.class);
        User u2 = mock(User.class);

        when(u1.getName()).thenReturn("Anna");
        when(u2.getName()).thenReturn("Bob");

        Set<Chat> chats1 = new HashSet<>();
        Set<Chat> chats2 = new HashSet<>();
        when(u1.getChats()).thenReturn(chats1);
        when(u2.getChats()).thenReturn(chats2);

        // Act
        Chat chat = factory.createDirectChat(u1, u2);

        // Assert
        assertEquals("Anna & Bob", chat.getChatName());
        assertEquals("DIRECT", chat.getChatType());
        assertEquals(2, chat.getParticipants().size());
        assertTrue(chat.getParticipants().contains(u1));
        assertTrue(chat.getParticipants().contains(u2));
        assertTrue(chats1.contains(chat));
        assertTrue(chats2.contains(chat));
    }

    @Test
    void createGroupChat_addsCreatorAndAllParticipants() {
        User creator = mock(User.class);
        when(creator.getChats()).thenReturn(new HashSet<>());

        User p1 = mock(User.class);
        User p2 = mock(User.class);
        when(p1.getChats()).thenReturn(new HashSet<>());
        when(p2.getChats()).thenReturn(new HashSet<>());

        Set<User> participants = new HashSet<>();
        participants.add(p1);
        participants.add(p2);

        Chat chat = factory.createGroupChat("Study Group", creator, participants);

        assertEquals("Study Group", chat.getChatName());
        assertEquals("GROUP", chat.getChatType());
        assertEquals(3, chat.getParticipants().size());
        assertTrue(chat.getParticipants().contains(creator));
        assertTrue(chat.getParticipants().contains(p1));
        assertTrue(chat.getParticipants().contains(p2));
    }

    @Test
    void createSystemChat_createsSystemChatWithNoParticipants() {
        Chat chat = factory.createSystemChat("Announcements");

        assertEquals("Announcements", chat.getChatName());
        assertEquals("SYSTEM", chat.getChatType());
        assertTrue(chat.getParticipants().isEmpty());
    }

    @Test
    void createEmptyGroupChat_addsOnlyCreator() {
        User creator = mock(User.class);
        when(creator.getChats()).thenReturn(new HashSet<>());

        Chat chat = factory.createEmptyGroupChat("New Group", creator);

        assertEquals("New Group", chat.getChatName());
        assertEquals("GROUP", chat.getChatType());
        assertEquals(1, chat.getParticipants().size());
        assertTrue(chat.getParticipants().contains(creator));
    }
}

