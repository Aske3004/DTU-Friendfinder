package com.friendfinder.repository;

import com.friendfinder.model.FriendRequest;
import com.friendfinder.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "friendrequest", path = "friendrequest")
public interface FriendRequestRepository extends PagingAndSortingRepository<FriendRequest, Long>, CrudRepository<FriendRequest, Long> {

    Iterable<FriendRequest> findBySender(User sender);
    Iterable<FriendRequest> findByReceiver(User receiver);

}
