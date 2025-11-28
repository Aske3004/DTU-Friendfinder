package com.friendfinder.services;

import com.friendfinder.exceptions.InvalidEmailException;
import com.friendfinder.exceptions.InvalidNameException;
import com.friendfinder.exceptions.InvalidPasswordException;
import com.friendfinder.model.User;
import com.friendfinder.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AuthenticatorService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void register(User user) throws InvalidEmailException, InvalidNameException, InvalidPasswordException, NullPointerException {
        // TODO Consider validating password strength more thoroughly
        //  Also consider sending a verification email?

        if (user == null) {
            throw new NullPointerException("User cannot be null");
        }

        user.setEmail(user.getEmail().toLowerCase());

        User.isNameValid(user.getName());
        User.isEmailValid(user.getEmail());
        User.isPasswordValid(user.getPassword());

        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new InvalidEmailException("User already exists with email: " + user.getEmail());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
    }

    public Auth authenticate(String email, String password) throws InvalidEmailException, InvalidPasswordException {
        if (email == null || email.isEmpty()) {
            throw new InvalidEmailException("Email cannot be null or empty");
        }

        if (password == null || password.isEmpty()) {
            throw new InvalidPasswordException("Password cannot be null or empty");
        }

        User user = userRepository.findByEmail(email.toLowerCase());

        if (user == null) {
            throw new InvalidEmailException("No such user: " + email);
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidPasswordException("Invalid password");
        }

        System.out.println("Authenticated as " + user.getEmail());

        return new Auth(user);
    }

    public record Auth(User user) { }
}
