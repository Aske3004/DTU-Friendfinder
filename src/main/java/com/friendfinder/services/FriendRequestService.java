package com.friendfinder.services;

import com.friendfinder.model.FriendRequest;
import com.friendfinder.model.User;
import com.friendfinder.repository.FriendRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FriendRequestService {

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    public Iterable<FriendRequest> findBySender(User sender) {
        return friendRequestRepository.findBySender(sender);
    }

    public Iterable<FriendRequest> findByReceiver(User receiver) {
        return friendRequestRepository.findByReceiver(receiver);
    }

    public FriendRequest sendFriendRequest(FriendRequest friendRequest) {
        return friendRequestRepository.save(friendRequest);
    }


}
