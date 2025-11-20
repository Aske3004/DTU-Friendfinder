package com.friendfinder.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class MessageReadReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "read_at", nullable = false)
    private LocalDateTime readAt;

    // JPA required constructor
    public MessageReadReceipt() {
    }

    // full constructor
    public MessageReadReceipt(Message message, User user, LocalDateTime readAt) {
        this.message = message;
        this.user = user;
        this.readAt = readAt;
    }

    // getters and setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Message getMessage() {
        return message;
    }
    public void setMessage(Message message) {
        this.message = message;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public LocalDateTime getReadAt() {
        return readAt;
    }
    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    @Override
    public String toString() {
        return "MessageReadReceipt{" +
                "id=" + id +
                ", messageId=" + (message != null ? message.getMessageId() : null) +
                ", userId=" + (user != null ? user.getUserId() : null) +
                ", readAt=" + readAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageReadReceipt that = (MessageReadReceipt) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
