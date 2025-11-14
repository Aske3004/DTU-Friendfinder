package com.friendfinder.services;

import com.friendfinder.dto.MessageDTO;
import com.friendfinder.model.Chat;
import com.friendfinder.model.Message;
import com.friendfinder.model.User;
import com.friendfinder.repository.ChatRepository;
import com.friendfinder.repository.MessageRepository;
import com.friendfinder.repository.UserRepository;
import com.friendfinder.strategy.MessageTypeStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageProcessor messageProcessor;
    private final ChatService chatService;

    @Autowired
    public MessageService(MessageRepository messageRepository, UserRepository userRepository,
                          ChatRepository chatRepository, MessageProcessor messageProcessor, ChatService chatService) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.messageProcessor = messageProcessor;
        this.chatService = chatService;
    }

    public MessageDTO saveMessage(MessageDTO messageDTO) {
        Chat chat = chatRepository.findById(messageDTO.getChatId())
                .orElseThrow(() -> new RuntimeException("Chat with id " + messageDTO.getChatId() + "not found"));
        User sender = userRepository.findById(messageDTO.getSenderId())
                .orElseThrow(() -> new RuntimeException("User with id " + messageDTO.getSenderId() + "not found"));

        // check if sender is part of chat
        if (!chatService.isUserInChat(messageDTO.getChatId(), messageDTO.getSenderId())) {
            throw new RuntimeException("User with id " + messageDTO.getSenderId() + " is not part of chat " + messageDTO.getChatId());
        }

        Message message = new Message(chat, sender, messageDTO.getContent(), messageDTO.getType().toString());
        message.setTimestamp(LocalDateTime.now());

        // Process message based on its type #Strategy pattern
        messageProcessor.processMessage(message);

        Message savedMessage = messagerepository.save(message);
        return new MessageDTO(savedMessage);

    }

    // get full chat
    public List<MessageDTO> getChatMessages(Long chatId, Long requestingUserId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat with id " + chatId + "not found"));

        if(!chatService.isUserInChat(chatId, requstingUserId)) {
            throw new RuntimeException("User with id " + requestingUserId + " is not part of chat " + chatId);
        }

        List<Message> messages = messageRepository.findByChatOrderByTimestampDesc(chat);

        return messages.stream()
                .map(MessageDTO::new)
                .collect(Collectors.toList());
    }

    // get specific message by id
    public MessageDTO getMessageById(Long messageId, Long requestingUserId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message with id " + messageId + "not found"));

        if(!chatService.isUserInChat(message.getChat().getChatId(), requestingUserId)) {
            throw new RuntimeException("User with id " + requestingUserId + " is not part of chat " + message.getChat().getChatId());
        }

        return new MessageDTO(message);
    }

    // edit message
    public MessageDTO editMessage(Long messageId, Long userId, String newContent) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message with id " + messageId + "not found"));

        if (!isUserMessageCreator(messageId, userId)) {
            throw new RuntimeException("User with id " + userId + " is not the sender of message " + messageId);
        }

        message.setContent(newContent);
        message.markAsEdited();

        Message updatedMessage = messagerepository.save(message);
        return new MessageDTO(updatedMessage);
    }

    // delete message
    public void deleteMessage(Long messageId, Long requestingUserId) {
        Message message = messagerepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message with id " + messageId + "not found"));

        // only sender can delete message
        if (!isUserMessageCreator(messageId, userId)) {
            throw new RuntimeException("User with id " + requestingUserId + " is not the sender of message " + messageId);
        }

        messagerepository.delete(message);
    }



}
