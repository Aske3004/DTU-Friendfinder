package com.friendfinder.dto.request;

import com.friendfinder.dto.MessageDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SendMessageRequest {

    @NotBlank(message = "Message content is required")
    @Size(max = 5000, message = "Message content must not exceed 5000 characters")
    private String content;

    private MessageDTO.MessageType type = MessageDTO.MessageType.CHAT;

    public SendMessageRequest() {
    }

    public SendMessageRequest(String content) {
        this.content = content;
        this.type = MessageDTO.MessageType.CHAT;
    }

    public SendMessageRequest(String content, MessageDTO.MessageType type) {
        this.content = content;
        this.type = type;
    }



    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageDTO.MessageType getType() {
        return type;
    }

    public void setType(MessageDTO.MessageType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SendMessageRequest{" +
                "content='" + content + '\'' +
                ", type=" + type +
                '}';
    }
}
