package com.friendfinder.services;

import com.friendfinder.dto.MessageDTO;
import com.friendfinder.dto.MessageReadReceiptDTO;
import com.friendfinder.model.Chat;
import com.friendfinder.model.Message;
import com.friendfinder.model.MessageReadReceipt;
import com.friendfinder.model.User;
import com.friendfinder.repository.ChatRepository;
import com.friendfinder.repository.MessageReadReceiptRepository;
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
    private final MessageReadReceiptRepository readReceiptRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, UserRepository userRepository,
                          ChatRepository chatRepository, MessageProcessor messageProcessor, ChatService chatService, MessageReadReceiptRepository readReceiptRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.messageProcessor = messageProcessor;
        this.chatService = chatService;
        this.readReceiptRepository = readReceiptRepository;
    }

    // verify access #Helper
    private void verifyUserAccessToMessage(Long chatId, Long userId) {
        if(!chatService.isUserInChat(chatId, userId)) {
            throw new RuntimeException("User with id " + userId + " is not part of chat " + chatId);
        }
    }

    // check if user is the creator of the message #Helper
    public boolean isUserMessageCreator(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message with id " + messageId + "not found"));

        return message.getSender().getUserId().equals(userId);
    }

    // get a message with auth #Helper
    private Message getMessageWithAuth(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message with id " + messageId + "not found"));

        verifyUserAccessToMessage(message.getChat().getChatId(), userId);

        return message;
    }


    public MessageDTO saveMessage(MessageDTO messageDTO, Long authenticatedUserId) {
        Chat chat = chatRepository.findById(messageDTO.getChatId())
                .orElseThrow(() -> new RuntimeException("Chat with id " + messageDTO.getChatId() + "not found"));
        User sender = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new RuntimeException("User with id " + authenticatedUserId + "not found"));

        // check if sender is part of chat
        verifyUserAccessToMessage(messageDTO.getChatId(), authenticatedUserId);

        Message message = new Message(chat, sender, messageDTO.getContent(), messageDTO.getType() != null ? messageDTO.getType().toString() : "CHAT");
        message.setTimestamp(LocalDateTime.now());

        // Process message based on its type #Strategy pattern
        messageProcessor.processMessage(message);

        Message savedMessage = messageRepository.save(message);
        return new MessageDTO(savedMessage);

    }

    // get full chat
    public List<MessageDTO> getChatMessages(Long chatId, Long requestingUserId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat with id " + chatId + "not found"));

        verifyUserAccessToMessage(chatId, requestingUserId);

        List<Message> messages = messageRepository.findByChatOrderByTimestampDesc(chat);

        return messages.stream()
                .map(MessageDTO::new)
                .collect(Collectors.toList());
    }

    // get specific message by id
    public MessageDTO getMessageById(Long messageId, Long requestingUserId) {
        Message message = getMessageWithAuth(messageId, requestingUserId);
        return new MessageDTO(message);
    }

    // edit message
    public MessageDTO editMessage(Long messageId, Long userId, String newContent) {
        if (!isUserMessageCreator(messageId, userId)) {
            throw new RuntimeException("User with id " + userId + " is not the sender of message " + messageId);
        }

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message with id " + messageId + "not found"));

        message.setContent(newContent);
        message.markAsEdited();

        Message updatedMessage = messageRepository.save(message);
        return new MessageDTO(updatedMessage);
    }

    // delete message
    public void deleteMessage(Long messageId, Long requestingUserId) {
        if (!isUserMessageCreator(messageId, requestingUserId)) {
            throw new RuntimeException("User with id " + requestingUserId + " is not the sender of message " + messageId);
        }

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message with id " + messageId + "not found"));

        messageRepository.delete(message);
    }

    // mark as read by a specific user
    public void markMessageAsread(Long messageId, Long userId) {
        Message message = getMessageWithAuth(messageId, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with id " + userId + "not found"));

        // sender cant mark as read
        if (isUserMessageCreator(messageId, userId)) {
            return;
        }

        // avoid duplicate entries
        if (readReceiptRepository.existsByMessageAndUser(message, user)) {
            return; // already marked as read
        }

        MessageReadReceipt receipt = new MessageReadReceipt(message, user, LocalDateTime.now());
        readReceiptRepository.save(receipt);

    }

    // get read receipts for a message
    public List<MessageReadReceiptDTO> getMessageReadRecepits(Long messageId, Long requestingUserId) {
        Message message = getMessageWithAuth(messageId, requestingUserId);

        List<MessageReadReceipt> receipts = readReceiptRepository.findByMessage(message);
        return receipts.stream()
                .map(MessageReadReceiptDTO::new)
                .collect(Collectors.toList());
    }



    // get unread messages
    public List<MessageDTO> getUnreadMessages(Long chatId, Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat with id " + chatId + "not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with id " + userId + "not found"));

        verifyUserAccessToMessage(chatId, userId);

        List<Message> unreadMessages = messageRepository.findUnreadMessagesByUserInChat(chat, user);

        return unreadMessages.stream()
                .map(MessageDTO::new)
                .collect(Collectors.toList());
    }

    // count unread messages
    public int getUnreadMessageCount(Long chatId, Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat with id " + chatId + "not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with id " + userId + "not found"));

        verifyUserAccessToMessage(chatId, userId);

        return messageRepository.countUnreadMessagesByUserInChat(chat, user);

    }



}
