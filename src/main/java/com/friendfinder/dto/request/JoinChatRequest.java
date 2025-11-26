package com.friendfinder.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public class JoinChatRequest {

    @NotNull(message = "Chat ID is required")
    private Long chatID;

    @NotNull(message = "User ID is required")
    private Long userID;

    @NotBlank(message = "Username is required")
    private String username;

    public JoinChatRequest() {
    }

    public JoinChatRequest(Long chatID, Long userID, String username) {
        this.chatID = chatID;
        this.userID = userID;
        this.username = username;
    }

    public Long getChatID() {
        return chatID;
    }

    public void setChatID(Long chatID) {
        this.chatID = chatID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "JoinChatRequest{" +
                "chatID=" + chatID +
                ", userID=" + userID +
                ", username='" + username + '\'' +
                '}';
    }
}
