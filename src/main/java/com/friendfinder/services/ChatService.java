package com.friendfinder.services;

import com.friendfinder.dto.ChatDTO;
import com.friendfinder.dto.MessageDTO;
import com.friendfinder.exceptions.ChatNotFoundException;
import com.friendfinder.exceptions.DuplicateParticipantException;
import com.friendfinder.exceptions.UserNotFoundException;
import com.friendfinder.exceptions.UserNotInChatException;
import com.friendfinder.factory.ChatFactory;
import com.friendfinder.model.Chat;
import com.friendfinder.model.Message;
import com.friendfinder.model.User;
import com.friendfinder.repository.ChatRepository;
import com.friendfinder.repository.MessageRepository;
import com.friendfinder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ChatFactory chatFactory;

    @Autowired
    public ChatService(ChatRepository chatRepository, UserRepository userRepository,
                       MessageRepository messageRepository, ChatFactory chatFactory) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.chatFactory = chatFactory;
    }
    // Newest message in id specific chat
    public ChatDTO getChatById(Long chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat" + chatId + "not found"));

        Message lastMessage = messageRepository.findTopByChatOrderByTimestampDesc(chat);
        MessageDTO lastMessageDTO = lastMessage != null ? new MessageDTO(lastMessage) : null;

        return new ChatDTO(chat, lastMessageDTO);
    }

    // list of chats from specific user
    public List<ChatDTO> getUserChats(Long userId) {
        List<Chat> chats = chatRepository.findByParticipantsUserId(userId);

        return chats.stream()
                .map(chat -> {
                    Message lastMessage = messageRepository.findTopByChatOrderByTimestampDesc(chat);
                    MessageDTO lastMessageDTO = lastMessage != null ? new MessageDTO(lastMessage) : null;
                    return new ChatDTO(chat, lastMessageDTO);
                })
                .collect(Collectors.toList());
    }

    // create new chat
    public ChatDTO createDirectChat(Long user1Id, Long user2Id) {
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new UserNotFoundException("User" + user1Id + "not found"));
        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new UserNotFoundException("User" + user2Id + "not found"));

        // Check if dm already exists
        List<Chat> user1Chats = chatRepository.findByParticipantsUserId(user1Id);
        for (Chat chat : user1Chats) {
            if ("DIRECT".equals(chat.getChatType()) && chat.getParticipants().contains(user2)) {
                return new ChatDTO(chat);
            }
        }

        Chat chat = chatFactory.createDirectChat(user1, user2);
        Chat savedChat = chatRepository.save(chat);

        return new ChatDTO(savedChat);
    }

    // create group chat
    public ChatDTO createGroupChat(String chatName, Long creatorId, Set<Long> participantIds) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new UserNotFoundException("Creator" + creatorId + "not found"));

        Set<User> participants = participantIds.stream()
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new UserNotFoundException("User" + id + "not found")))
                .collect(Collectors.toSet());

        Chat chat = chatFactory.createGroupChat(chatName, creator, participants);
        Chat savedChat = chatRepository.save(chat);

        return new ChatDTO(savedChat);
    }

    // add person to chat
    public void addParticipantToChat(Long chatId, Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat" + chatId + "not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User" + userId + "not found"));

        // check if user is part of gc
        if (isUserInChat(chatId, userId)) {
            throw new DuplicateParticipantException("User" + userId + "is already in group chat");
        }

        chat.getParticipants().add(user);
        chatRepository.save(chat);
    }

    // remove person from chat
    public void removeParticipantFromChat(Long chatId, Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat" + chatId + "not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User" + userId + "not found"));

        // check if user is part of gc
        if (!isUserInChat(chatId, userId)) {
            throw new UserNotInChatException("User" + userId + "is not in group chat");
        }

        chat.getParticipants().remove(user);
        chatRepository.save(chat);
    }

    // delete a chat
    public void deleteChat(Long chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat" + chatId + "not found"));

        chatRepository.delete(chat);
    }

    public boolean isUserInChat(Long chatId, Long userId) {
        if (chatId == null || userId == null) {
            return false;
        }
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat" + chatId + "not found"));

        return chat.getParticipants().stream()
                .anyMatch(user -> user.getUserId().equals(userId));
    }


}
