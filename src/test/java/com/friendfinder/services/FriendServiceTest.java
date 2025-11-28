package com.friendfinder.services;

import com.friendfinder.model.FriendRequest;
import com.friendfinder.model.User;
import com.friendfinder.repository.FriendRequestRepository;
import com.friendfinder.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

// TODO
//  already friends test
//  already request pending test

@ActiveProfiles("test")
@SpringBootTest
@Transactional  // roll back after each test
class FriendServiceTest {

    @Autowired
    private FriendService friendService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private FriendRequestRepository requestRepo;

    private User sender;
    private User receiver;
    private FriendRequest request;

    @BeforeEach
    void setUp() {
        // Create sender on each test
        sender = new User();
        sender.setEmail("sender@dtu.dk");
        sender.setName("Sender");
        sender = userRepo.save(sender);

        // Create receiver on each test
        receiver = new User();
        receiver.setEmail("receiver@dtu.dk");
        receiver.setName("Receiver");
        receiver = userRepo.save(receiver);

        // Create pending request on each test
        request = new FriendRequest(sender, receiver);
        request = requestRepo.save(request);
    }

    @Test
    void testPendingRequest() {
        // Slet request fra setup
        requestRepo.delete(request);

        assertEquals(0, requestRepo.count());

        friendService.sendRequest(sender, receiver);

        // Assert request findes i DB
        assertNotNull(requestRepo.findBySenderAndReceiver(sender, receiver));

        // Check sender has 0 as receiver
        assertTrue(requestRepo.findByReceiver(sender).isEmpty());
        assertTrue(friendService.getPendingRequestsAsReceiver(sender).isEmpty());
        // Check sender has 1 as sender
        assertEquals(1, requestRepo.findBySender(sender).size());
        assertEquals(1, friendService.getPendingRequestsAsSender(sender).size());
        // Check receiver has 0 as sender
        assertTrue(requestRepo.findBySender(receiver).isEmpty());
        assertTrue(friendService.getPendingRequestsAsSender(receiver).isEmpty());
        // Check receiver has 1 as receiver
        assertEquals(1, requestRepo.findByReceiver(receiver).size());
        assertEquals(1, friendService.getPendingRequestsAsReceiver(receiver).size());
    }

    @Test
    void testAcceptRequest() {
        // Assert request fra setup findes i DB
        assertNotNull(requestRepo.findById(request.getId()));

        // Accept request
        friendService.acceptRequest(request.getId());

        // Get updated request and check it's accepted
        FriendRequest updated = requestRepo.findById(request.getId()).orElseThrow();
        assertTrue(updated.isAccepted());

        // Get updated sender and receiver
        User updatedSender = userRepo.findById(sender.getUserId()).orElseThrow();
        User updatedReceiver = userRepo.findById(receiver.getUserId()).orElseThrow();

        // Assert sender and receiver are friends
        assertTrue(updatedSender.getFriends().contains(updatedReceiver));
        assertTrue(updatedReceiver.getFriends().contains(updatedSender));
        assertTrue(friendService.areFriends(sender, receiver));
        assertTrue(friendService.areFriends(receiver, sender));
        assertTrue(friendService.getFriends(sender).contains(receiver));
        assertTrue(friendService.getFriends(receiver).contains(sender));
    }

    @Test
    void removeFriend_shouldDeleteFriendFromBothLists() {
        // set up friendship in DB (Tested above)
        friendService.acceptRequest(request.getId());


        // remove friendship
        friendService.removeFriend(sender, receiver);

        // reload from DB to assert real state
        User updatedSender = userRepo.findById(sender.getUserId()).orElseThrow();
        User updatedReceiver = userRepo.findById(receiver.getUserId()).orElseThrow();

        // assert not friends
        assertFalse(updatedSender.getFriends().contains(updatedReceiver));
        assertFalse(updatedReceiver.getFriends().contains(updatedSender));
    }

    @Test
    void declineRequest_shouldDeleteRequestAndNotCreateFriendship() {
        // precondition: request findes i DB
        assertTrue(requestRepo.findById(request.getId()).isPresent());

        // act
        friendService.declineRequest(request.getId());

        // assert – request er SLETTET
        assertTrue(requestRepo.findById(request.getId()).isEmpty(),
                "Friend request should be deleted after decline");

        // reload users fra DB
        User updatedSender = userRepo.findById(sender.getUserId()).orElseThrow();
        User updatedReceiver = userRepo.findById(receiver.getUserId()).orElseThrow();

        // og der er stadig ingen venner
        assertFalse(updatedSender.getFriends().contains(updatedReceiver),
                "Sender should NOT have receiver in friend list");
        assertFalse(updatedReceiver.getFriends().contains(updatedSender),
                "Receiver should NOT have sender in friend list");
    }

    @Test
    void sendFriendRequest_shouldNotCreateRequest_whenUsersAreAlreadyFriends() {
        // Accept the pending request (Tested above)
        friendService.acceptRequest(request.getId());

        long initialRequestCount = requestRepo.count();

        // Both try to send a new request between the same users
        friendService.sendRequest(sender, receiver);   // sender -> receiver
        friendService.sendRequest(receiver, sender);   // receiver -> sender

        // Assert no extra friend requests were created
        long finalRequestCount = requestRepo.count();
        assertEquals(initialRequestCount, finalRequestCount,
                "No new friend requests should be created when users are already friends");
    }

    @Test
    void sendFriendRequest_shouldNotCreateDuplicate_whenPendingRequestAlreadyExists() {
        // Setup already created a pending request
        long initialRequestCount = requestRepo.count();

        // Both sides try to create another request while one is already pending
        friendService.sendRequest(sender, receiver);   // same direction
        friendService.sendRequest(receiver, sender);   // opposite direction

        // Assert it's still only the original pending request in the DB
        long finalRequestCount = requestRepo.count();
        assertEquals(initialRequestCount, finalRequestCount,
                "No additional friend requests should be created when one is already pending");

        // Also assert they are still NOT friends
        User updatedSender = userRepo.findById(sender.getUserId()).orElseThrow();
        User updatedReceiver = userRepo.findById(receiver.getUserId()).orElseThrow();
        assertFalse(updatedSender.getFriends().contains(updatedReceiver));
        assertFalse(updatedReceiver.getFriends().contains(updatedSender));
    }
}


