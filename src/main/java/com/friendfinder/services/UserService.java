package com.friendfinder.services;

import com.friendfinder.exceptions.InvalidEmailException;
import com.friendfinder.exceptions.InvalidNameException;
import com.friendfinder.model.FriendRequest;
import com.friendfinder.model.Interest;
import com.friendfinder.model.User;
import com.friendfinder.repository.FriendRequestRepository;
import com.friendfinder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendRequestRepository friendRequestRepository;

    public Iterable<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findUser(String email) {return userRepository.findByEmail(email.toLowerCase());}

    @Transactional
    public User updateUserName(@Param("name") String name, @Param("email") String email) throws InvalidNameException {
        User user = userRepository.findByEmail(email.toLowerCase());
        User.isNameValid(name);
        user.setName(name);
        return userRepository.save(user);
    }


    @Transactional
    public void deleteUser(@Param("email") String email) {
        User user = userRepository.findByEmail(email.toLowerCase());
        userRepository.delete(user);
        return;
    }

    @Transactional
    public void updateUserInterest(@Param("interest") List<Interest> interests, @Param("email") String email) {
        User user = userRepository.findByEmail(email.toLowerCase());
        user.setInterests(interests);
        userRepository.save(user);
        return;
    }

    @Transactional
    public List<User> findPotentialFriends(String email, List<String> disliked) {
        User user = userRepository.findByEmail(email.toLowerCase());

        List<User> allUsers = (List<User>) userRepository.findAll();
        allUsers.remove(user);

        List<User> friends = user.getFriends();
        allUsers.removeAll(friends);

        List<FriendRequest> addedFriends = friendRequestRepository.findBySender(user);
        List<User> addedUsers = new ArrayList<>();
        for(FriendRequest friendRequest : addedFriends) {
            addedUsers.add(friendRequest.getReceiver());
        }

        allUsers.removeAll(addedUsers);

        if (disliked != null) {
            allUsers.removeIf(u -> disliked.contains(u.getEmail()));
        }


        List<Interest> userInterests = user.getInterests();

        return allUsers.stream()
                .sorted((u1, u2) -> {
                    long matches1 = u1.getInterests().stream()
                            .filter(userInterests::contains)
                            .count();
                    long matches2 = u2.getInterests().stream()
                            .filter(userInterests::contains)
                            .count();
                    return Long.compare(matches2, matches1); // descending order
                })
                .toList();
    }
}
