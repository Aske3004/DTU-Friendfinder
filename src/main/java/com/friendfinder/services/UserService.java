package com.friendfinder.services;

import com.friendfinder.model.User;
import com.friendfinder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Iterable<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findUser(String email) {return userRepository.findByEmail(email.toLowerCase());}

    @Transactional
    public User updateUserName(@Param("name") String name, @Param("email") String email) {
        User user = userRepository.findByEmail(email.toLowerCase());
        user.setName(name);
        return userRepository.save(user);
    }

    @Transactional
    public User updateUserEmail(@Param("newemail") String newemail, @Param("email") String email) {
        User user = userRepository.findByEmail(email.toLowerCase());
        user.setEmail(newemail);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(@Param("email") String email) {
        User user = userRepository.findByEmail(email.toLowerCase());
        userRepository.delete(user);
        return;
    }
}
