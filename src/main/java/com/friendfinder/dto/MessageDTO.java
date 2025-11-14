package com.friendfinder.dto;

import com.friendfinder.model.Message;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MessageDTO {
    private Long messageId;
    private Long chatId;
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime timestamp;
    private MessageType type;
    private Boolean isEdited;
    private LocalDateTime editedAt;
    private List<MessageReadReceiptDTO> readReceipts;
    private Integer readCount;

    public enum MessageType {
        CHAT, JOIN, LEAVE, SYSTEM
    }

    public MessageDTO() {
    } // Required for JSON deserialization

    public MessageDTO(Message message) {
        this.messageId = message.getMessageId();

        if (message.getChat() != null) {
            this.chatId = message.getChat().getChatId();
        }

        if (message.getSender() != null) {
            this.senderId = message.getSender().getUserId();
            this.senderName = message.getSender().getName();
        }

        this.content = message.getContent();
        this.timestamp = message.getTimestamp();

        try {
            this.type = message.getMessageType() != null
                    ? MessageType.valueOf(message.getMessageType())
                    : MessageType.CHAT;
        } catch (IllegalArgumentException e) {
            this.type = MessageType.CHAT; // Default isnt a type so if no type it is CHAT
        }

        this.isEdited = message.getIsEdited();
        this.editedAt = message.getEditedAt();

        if (message.getReadReceipts() != null) {
            this.readReceipts = message.getReadReceipts().stream()
                    .map(MessageReadReceiptDTO::new)
                    .collect(Collectors.toList());
            this.readCount = message.getReadCount();
        } else {
            this.readReceipts = new ArrayList<>();
            this.readCount = 0;
        }
    }

    // full manual constructor
    public MessageDTO(Long messageId, Long chatId, Long senderId, String senderName,
                      String content, LocalDateTime timestamp, MessageType type,
                      Boolean isEdited,
                      List<MessageReadReceiptDTO> readReceipts, Integer readCount) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.timestamp = timestamp;
        this.type = type;
        this.isEdited = false;
        this.readReceipts = new ArrayList<>();
        this.readCount = 0;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getChatId() {
        return chatId;
    }
    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
    public Long getSenderId() {
        return senderId;
    }
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
    public String getSenderName() {
        return senderName;
    }
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public MessageType getType() {
        return type;
    }
    public void setType(MessageType type) {
        this.type = type;
    }
    public Boolean getIsEdited() {
        return isEdited;
    }
    public void setIsEdited(Boolean isEdited) {
        this.isEdited = isEdited;
    }
    public LocalDateTime getEditedAt() {
        return editedAt;
    }
    public void setEditedAt(LocalDateTime editedAt) {
        this.editedAt = editedAt;
    }
    public List<MessageReadReceiptDTO> getReadReceipts() {
        return readReceipts;
    }
    public void setReadReceipts(List<MessageReadReceiptDTO> readReceipts) {
        this.readReceipts = readReceipts;
    }
    public Integer getReadCount() {
        return readCount;
    }
    public void setReadCount(Integer readCount) {
        this.readCount = readCount;
    }
}
