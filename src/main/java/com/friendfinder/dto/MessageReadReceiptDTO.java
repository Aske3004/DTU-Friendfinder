package com.friendfinder.dto;

import com.friendfinder.model.MessageReadReceipt;
import java.time.LocalDateTime;

public class MessageReadReceiptDTO {
    private Long userId;
    private String username;
    private LocalDateTime readAt;

    public MessageReadReceiptDTO() {
    } // for JSON deserialization

    // Entity constructor
    public MessageReadReceiptDTO(MessageReadReceipt receipt) {
        if (receipt.getUser() != null) {
            this.userId = receipt.getUser().getUserId();
            this.username = receipt.getUser().getName();
        }
        this.readAt = receipt.getReadAt();
    }

    // full constructor
    public MessageReadReceiptDTO(Long userId, String username, LocalDateTime readAt) {
        this.userId = userId;
        this.username = username;
        this.readAt = readAt;
    }

    // getters and setters
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public LocalDateTime getReadAt() {
        return readAt;
    }
    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    @Override
    public String toString() {
        return "MessageReadReceiptDTO{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", readAt=" + readAt +
                '}';
    }


}
