package com.friendfinder.services;

import com.friendfinder.model.FriendRequest;
import com.friendfinder.model.User;
import com.friendfinder.repository.FriendRequestRepository;
import com.friendfinder.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendService {

    @Autowired
    private FriendRequestRepository requestRepo;

    @Autowired
    private UserRepository userRepo;

    public List<FriendRequest> getPendingRequests(User receiver) {
        return requestRepo.findByReceiver(receiver)
                .stream()
                .filter(req -> !req.isAccepted())
                .toList();
    }

    @Transactional
    public void sendRequest(User sender, User receiver) {
        if (requestRepo.findBySenderAndReceiver(sender, receiver) != null) return;
        requestRepo.save(new FriendRequest(sender, receiver));
    }

    @Transactional
    public void acceptRequest(Long requestId) {
        FriendRequest req = requestRepo.findById(requestId).orElse(null);
        if (req != null && !req.isAccepted()) {
            req.setAccepted(true);
            User sender = req.getSender();
            User receiver = req.getReceiver();
            sender.addFriend(receiver);
            receiver.addFriend(sender);
            userRepo.save(sender);
            userRepo.save(receiver);
            requestRepo.save(req);
        }
    }

    @Transactional
    public void declineRequest(Long requestId) {
        requestRepo.deleteById(requestId);
    }

    @Transactional
    public List<User> getFriends(User user) {
        User managedUser = userRepo.findById(user.getUserId()).orElse(null);
        return managedUser != null ? managedUser.getFriends() : List.of();
    }

    public boolean hasPendingRequest(User sender, User receiver) {
        return requestRepo.findBySenderAndReceiver(sender, receiver) != null;
    }
    public boolean areFriends(User user1, User user2) {
        User fullUser1 = userRepo.findById(user1.getUserId()).orElse(null);
        if (fullUser1 == null) return false;
        return fullUser1.getFriends().contains(user2);
    }

    @Transactional
    public void removeFriend(User user, User friend) {
        User managedUser = userRepo.findById(user.getUserId()).orElse(null);
        User managedFriend = userRepo.findById(friend.getUserId()).orElse(null);
        if (managedUser == null || managedFriend == null) return;
        managedUser.getFriends().remove(managedFriend);
        managedFriend.getFriends().remove(managedUser);

        userRepo.save(managedUser);
        userRepo.save(managedFriend);
    }

}
