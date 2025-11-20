package com.friendfinder.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import static com.friendfinder.model.FriendRequestStatus.*;

@Entity
@JsonIdentityInfo(
        scope = FriendRequest.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "uid"
)
public class FriendRequest {

    @Id
    @Column(name="friend_request_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long uid;

    @ManyToOne
    private User sender;

    @ManyToOne
    private User receiver;

    private FriendRequestStatus status = PENDING;

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getSender() {
        return sender;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public User getReceiver() {
        return receiver;
    }

    public FriendRequestStatus getStatus() {
        return status;
    }

    public void accept() {
        this.status = ACCEPTED;
    }

    public void reject() {
        this.status = REJECTED;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }
}
