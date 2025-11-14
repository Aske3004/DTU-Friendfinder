package com.friendfinder.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    @JsonIgnore
    private Chat chat;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User sender;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private LocalDateTime timestamp;
    private String messageType;

    private Boolean isEdited;
    private LocalDateTime editedAt;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<MessageReadReceipt> readReceipts = new ArrayList<>();

    // JPA required constructor
    public Message() {
        this.timestamp = LocalDateTime.now();
        this.messageType = "CHAT";
        this.isEdited = false;
    }

    // essential constructor
    public Message(Chat chat, User sender, String content) {
        this.chat = chat;
        this.sender = sender;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.messageType = "CHAT";
        this.isEdited = false;
    }

    // type constructor
    public Message(Chat chat, User sender, String content, String messageType) {
        this.chat = chat;
        this.sender = sender;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.messageType = messageType;
        this.isEdited = false;
    }

    // getters and setters
    public Long getMessageId() {
        return messageId;
    }
    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }
    public Chat getChat() {
        return chat;
    }
    public void setChat(Chat chat) {
        this.chat = chat;
    }
    public User getSender() {
        return sender;
    }
    public void setSender(User sender) {
        this.sender = sender;
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
    public String getMessageType() {
        return messageType;
    }
    public void setMessageType(String messageType) {
        this.messageType = messageType;
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
    public List<MessageReadReceipt> getReadReceipts() {
        return readReceipts;
    }
    public void setReadReceipts(List<MessageReadReceipt> readReceipts) {
        this.readReceipts = readReceipts;
    }

    // mark message as edited
    public void markAsEdited() {
        this.isEdited = true;
        this.editedAt = LocalDateTime.now();
    }

    // add read receipt
    public void addReadReceipt(MessageReadReceipt receipt) {
        this.readReceipts.add(receipt);
        receipt.setMessage(this);
    }

    // check if a user has read the message
    public boolean hasUserRead(User user) {
        return readReceipts.stream()
                .anyMatch(receipt -> receipt.getUser().equals(user));
    }

    // get count of users who have read the message
    public int getReadCount() {
        return readReceipts.size();
    }

    // check if system message
    public boolean isSystemMessage() {
        return "JOIN".equals(messageType) ||
                "LEAVE".equals(messageType) ||
                "SYSTEM".equals(messageType);
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", chatId=" + (chat != null ? chat.getChatId() : null) +
                ", senderId=" + (sender != null ? sender.getUserId() : null) +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", messageType='" + messageType + '\'' +
                ", isEdited=" + isEdited +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message message = (Message) o;
        return messageId != null && messageId.equals(message.getMessageId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
