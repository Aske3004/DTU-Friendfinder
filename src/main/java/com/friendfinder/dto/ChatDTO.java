package com.friendfinder.dto;


import com.friendfinder.model.Chat;
import com.friendfinder.model.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import java.util.List;


public class ChatDTO {
    private Long chatId;
    private String chatName;
    private String chatType;
    private LocalDateTime createdAt;
    private List<String> participantUsernames;
    private List<Long> participantIds;
    private MessageDTO lastMessage;
    private Integer unreadCount;
    private Integer participantCount;

    public ChatDTO() {
    } // Required for JSON deserialization

    // basic info constructor
    public ChatDTO(Chat chat) {
        this.chatId = chat.getChatId();
        this.chatName = chat.getChatName();
        this.chatType = chat.getChatType();
        this.createdAt = chat.getCreatedAt();

        if (chat.getParticipants() != null) {
            this.participantUsernames = chat.getParticipants().stream()
                    .map(User::getName)
                    .collect(Collectors.toList());
            this.participantIds = chat.getParticipants().stream()
                    .map(User::getUserId)
                    .collect(Collectors.toList());
            this.participantCount = chat.getParticipants().size();
        } else {
            this.participantUsernames = new ArrayList<>();
            this.participantIds = new ArrayList<>();
            this.participantCount = 0;
        }
    }

    // with last message constructor
    public ChatDTO(Chat chat, MessageDTO lastMessage) {
        this(chat);
        this.lastMessage = lastMessage;
    }

    // full constructor
    public ChatDTO(Long chatId, String chatName, String chatType,
                   LocalDateTime createdAt, List<String> participantUsernames,
                   List<Long> participantIds, MessageDTO lastMessage,
                   Integer unreadCount) {
        this.chatId = chatId;
        this.chatName = chatName;
        this.chatType = chatType;
        this.createdAt = createdAt;
        this.participantUsernames = participantUsernames;
        this.participantIds = participantIds;
        this.participantCount = participantIds != null ? participantIds.size() : 0;
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
    }

    // Getters and Setters
    public Long getChatId() {
        return chatId;
    }
    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
    public String getChatName() {
        return chatName;
    }
    public void setChatName(String chatName) {
        this.chatName = chatName;
    }
    public String getChatType() {
        return chatType;
    }
    public void setChatType(String chatType) {
        this.chatType = chatType;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public List<String> getParticipantUsernames() {
        return participantUsernames;
    }
    public void setParticipantUsernames(List<String> participantUsernames) {
        this.participantUsernames = participantUsernames;
    }
    public List<Long> getParticipantIds() {
        return participantIds;
    }
    public void setParticipantIds(List<Long> participantIds) {
        this.participantIds = participantIds;
    }
    public MessageDTO getLastMessage() {
        return lastMessage;
    }
    public void setLastMessage(MessageDTO lastMessage) {
        this.lastMessage = lastMessage;
    }
    public Integer getUnreadCount() {
        return unreadCount;
    }
    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }
    public Integer getParticipantCount() {
        return participantCount;
    }
    public void setParticipantCount(Integer participantCount) {
        this.participantCount = participantCount;
    }

}
