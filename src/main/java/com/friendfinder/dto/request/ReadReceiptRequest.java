package com.friendfinder.dto.request;

import jakarta.validation.constraints.NotNull;

public class ReadReceiptRequest {

    @NotNull(message = "Message ID is required")
    private Long messageID;

    @NotNull(message = "User ID is required")
    private Long userID;

    public ReadReceiptRequest() {
    }

    public ReadReceiptRequest(Long messageID, Long userID) {
        this.messageID = messageID;
        this.userID = userID;
    }

    public Long getMessageID() {
        return messageID;
    }

    public void setMessageID(Long messageID) {
        this.messageID = messageID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        return "ReadReceiptRequest{" +
                "messageID=" + messageID +
                ", userID=" + userID +
                '}';
    }
}
