package com.friendfinder.repository;

import com.friendfinder.model.FriendRequest;
import com.friendfinder.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRequestRepository extends CrudRepository<FriendRequest, Long> {
    List<FriendRequest> findByReceiver(User receiver);
    FriendRequest findBySenderAndReceiver(User sender, User receiver);

    List<FriendRequest> findBySender(User sender);
}
