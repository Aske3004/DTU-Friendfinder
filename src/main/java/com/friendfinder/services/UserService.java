package com.friendfinder.services;

import com.friendfinder.model.User;
import com.friendfinder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Iterable<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findUser(String email) {return userRepository.findByEmail(email.toLowerCase());}
}
