package com.friendfinder.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

public class CreateGroupChatRequest {

    @NotBlank(message = "Chat name is required")
    @Size(max = 100, message = "Chat name must not exceed 100 characters")
    private String chatName;

    @NotNull(message = "Creator ID is required")
    private Long creatorID;

    @NotEmpty(message = "At least one participant ID is required")
    private Set<Long> participantIDs = new HashSet<>();

    public CreateGroupChatRequest() {
    }

    public CreateGroupChatRequest(String chatName, Long creatorID, Set<Long> participantIDs) {
        this.chatName = chatName;
        this.creatorID = creatorID;
        this.participantIDs = participantIDs;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public Long getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(Long creatorID) {
        this.creatorID = creatorID;
    }

    public Set<Long> getParticipantIDs() {
        return participantIDs;
    }

    public void setParticipantIDs(Set<Long> participantIDs) {
        this.participantIDs = participantIDs;
    }

    @Override
    public String toString() {
        return "CreateGroupChatRequest{" +
                "chatName='" + chatName + '\'' +
                ", creatorID=" + creatorID +
                ", participantIDs=" + participantIDs +
                '}';
    }

}
