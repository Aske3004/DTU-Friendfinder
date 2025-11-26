package com.friendfinder.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatId;

    private String chatName;
    private LocalDateTime createdAt;
    private String chatType;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Message> messages = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "chat_participants",
        joinColumns = @JoinColumn(name = "chat_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants = new HashSet<>();

    // default constructor required by JPA
    public Chat() {
        this.createdAt = LocalDateTime.now();
    }

    // constructor with chat name
    public Chat(String chatName) {
        this(chatName, "GROUP");
    }

    // main constructor
    public Chat(String chatName, String chatType) {
        this.chatName = chatName;
        this.chatType = chatType;
        this.createdAt = LocalDateTime.now();
        this.participants = new HashSet<>();
    }

    // getters and Setters
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
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public String getChatType() {
        return chatType;
    }
    public void setChatType(String chatType) {
        this.chatType = chatType;
    }
    public List<Message> getMessages() {
        return messages;
    }
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
    public Set<User> getParticipants() {
        return participants;
    }
    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }

    // add message
    public void addMessage(Message message) {
        messages.add(message);
        message.setChat(this);
    }

    // delete message
    public void removeMessage(Message message) {
        messages.remove(message);
        message.setChat(null);
    }

    // add participant
    public void addParticipant(User user) {
        participants.add(user);
        user.getChats().add(this);
    }

    // remove participant
    public void removeParticipant(User user) {
        participants.remove(user);
        user.getChats().remove(this);
    }

    @Override
    public String toString() {
        return "Chat{" +
                "chatId=" + chatId +
                ", chatName='" + chatName + '\'' +
                ", chatType='" + chatType + '\'' +
                ", createdAt=" + createdAt +
                ", participantCount=" + (participants != null ? participants.size() : 0) +
                ", messagesCount=" + (messages != null ? messages.size() : 0) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Chat)) return false;
        Chat chat = (Chat) o;
        return chatId != null && chatId.equals(chat.getChatId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
