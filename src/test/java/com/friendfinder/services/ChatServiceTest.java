package com.friendfinder.services;

import com.friendfinder.dto.ChatDTO;
import com.friendfinder.exceptions.ChatNotFoundException;
import com.friendfinder.exceptions.DuplicateParticipantException;
import com.friendfinder.exceptions.UserNotFoundException;
import com.friendfinder.exceptions.UserNotInChatException;
import com.friendfinder.factory.ChatFactory;
import com.friendfinder.model.Chat;
import com.friendfinder.model.User;
import com.friendfinder.repository.ChatRepository;
import com.friendfinder.repository.MessageRepository;
import com.friendfinder.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatServiceTest {

    private ChatRepository chatRepository;
    private UserRepository userRepository;
    private MessageRepository messageRepository;
    private ChatFactory chatFactory;

    private ChatService chatService;

    @BeforeEach
    void setUp() {
        chatRepository = mock(ChatRepository.class);
        userRepository = mock(UserRepository.class);
        messageRepository = mock(MessageRepository.class);
        chatFactory = mock(ChatFactory.class);

        chatService = new ChatService(chatRepository, userRepository, messageRepository, chatFactory);
    }

    // ---------- getChatById ----------

    @Test
    void getChatById_throwsChatNotFound_whenChatMissing() {
        Long chatId = 1L;
        when(chatRepository.findById(chatId)).thenReturn(Optional.empty());

        assertThrows(ChatNotFoundException.class,
                () -> chatService.getChatById(chatId));
    }

    // ---------- createDirectChat ----------

    @Test
    void createDirectChat_throwsUserNotFound_whenFirstUserMissing() {
        Long user1Id = 1L;
        Long user2Id = 2L;
        when(userRepository.findById(user1Id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> chatService.createDirectChat(user1Id, user2Id));
    }

    @Test
    void createDirectChat_throwsUserNotFound_whenSecondUserMissing() {
        Long user1Id = 1L;
        Long user2Id = 2L;

        User user1 = mock(User.class);
        when(userRepository.findById(user1Id)).thenReturn(Optional.of(user1));
        when(userRepository.findById(user2Id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> chatService.createDirectChat(user1Id, user2Id));
    }

    // ---------- createGroupChat ----------

    @Test
    void createGroupChat_throwsUserNotFound_whenCreatorMissing() {
        Long creatorId = 10L;
        Set<Long> participantIds = Set.of(1L, 2L);

        when(userRepository.findById(creatorId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> chatService.createGroupChat("Group", creatorId, participantIds));
    }

    @Test
    void createGroupChat_throwsUserNotFound_whenParticipantMissing() {
        Long creatorId = 10L;
        Set<Long> participantIds = Set.of(1L, 2L);

        User creator = mock(User.class);
        when(userRepository.findById(creatorId)).thenReturn(Optional.of(creator));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mock(User.class)));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> chatService.createGroupChat("Group", creatorId, participantIds));
    }

    // ---------- addParticipantToChat ----------

    @Test
    void addParticipantToChat_throwsChatNotFound_whenChatMissing() {
        Long chatId = 1L;
        Long userId = 2L;

        when(chatRepository.findById(chatId)).thenReturn(Optional.empty());

        assertThrows(ChatNotFoundException.class,
                () -> chatService.addParticipantToChat(chatId, userId));
    }

    @Test
    void addParticipantToChat_throwsUserNotFound_whenUserMissing() {
        Long chatId = 1L;
        Long userId = 2L;

        Chat chat = mock(Chat.class);
        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> chatService.addParticipantToChat(chatId, userId));
    }

    @Test
    void addParticipantToChat_throwsDuplicateParticipant_whenUserAlreadyInChat() {
        Long chatId = 1L;
        Long userId = 2L;

        Chat chat = mock(Chat.class);
        User user = mock(User.class);

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        // isUserInChat() kaldes inde i metoden → stub den til true
        ChatService spyService = spy(chatService);
        doReturn(true).when(spyService).isUserInChat(chatId, userId);

        assertThrows(DuplicateParticipantException.class,
                () -> spyService.addParticipantToChat(chatId, userId));
    }

    // ---------- removeParticipantFromChat ----------

    @Test
    void removeParticipantFromChat_throwsChatNotFound_whenChatMissing() {
        Long chatId = 1L;
        Long userId = 2L;

        when(chatRepository.findById(chatId)).thenReturn(Optional.empty());

        assertThrows(ChatNotFoundException.class,
                () -> chatService.removeParticipantFromChat(chatId, userId));
    }

    @Test
    void removeParticipantFromChat_throwsUserNotFound_whenUserMissing() {
        Long chatId = 1L;
        Long userId = 2L;

        Chat chat = mock(Chat.class);
        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> chatService.removeParticipantFromChat(chatId, userId));
    }

    @Test
    void removeParticipantFromChat_throwsUserNotInChat_whenUserNotInGroup() {
        Long chatId = 1L;
        Long userId = 2L;

        Chat chat = mock(Chat.class);
        User user = mock(User.class);

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ChatService spyService = spy(chatService);
        doReturn(false).when(spyService).isUserInChat(chatId, userId);

        assertThrows(UserNotInChatException.class,
                () -> spyService.removeParticipantFromChat(chatId, userId));
    }

    // ---------- deleteChat ----------

    @Test
    void deleteChat_throwsChatNotFound_whenChatMissing() {
        Long chatId = 1L;
        when(chatRepository.findById(chatId)).thenReturn(Optional.empty());

        assertThrows(ChatNotFoundException.class,
                () -> chatService.deleteChat(chatId));
    }
}

