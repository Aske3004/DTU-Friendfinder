package com.friendfinder.services;

import com.friendfinder.model.FriendRequest;
import com.friendfinder.model.User;
import com.friendfinder.repository.FriendRequestRepository;
import com.friendfinder.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class FriendServiceTest {

    @Mock
    private FriendRequestRepository requestRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private FriendService friendService;

    private User sender;
    private User receiver;
    private FriendRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sender = new User();
        sender.setUserId(1L);
        sender.setEmail("sender@dtu.dk");
        sender.setName("Sender");
        sender.setFriends(new ArrayList<>());

        receiver = new User();
        receiver.setUserId(2L);
        receiver.setEmail("receiver@dtu.dk");
        receiver.setName("Receiver");
        receiver.setFriends(new ArrayList<>());

        request = new FriendRequest(sender, receiver);
        request.setAccepted(false);
    }

    @Test
    void testAcceptRequest() {

        when(requestRepo.findById(anyLong())).thenReturn(Optional.of(request));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));


        friendService.acceptRequest(1L);


        assertTrue(request.isAccepted(), "Friend request should be marked as accepted");
        assertTrue(sender.getFriends().contains(receiver), "Sender should have receiver in friend list");
        assertTrue(receiver.getFriends().contains(sender), "Receiver should have sender in friend list");

        // Verify that both users were saved
        verify(userRepo, times(2)).save(any(User.class));
        verify(requestRepo, times(1)).save(request);
    }
    @Test
    void removeFriend_shouldDeleteFriendFromBothLists() {
        sender.getFriends().add(receiver);
        receiver.getFriends().add(sender);

        when(userRepo.findById(sender.getUserId())).thenReturn(Optional.of(sender));
        when(userRepo.findById(receiver.getUserId())).thenReturn(Optional.of(receiver));

        friendService.removeFriend(sender, receiver);

        assertFalse(sender.getFriends().contains(receiver));
        assertFalse(receiver.getFriends().contains(sender));
        verify(userRepo, times(2)).save(any(User.class));
    }

}


