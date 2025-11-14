package com.friendfinder.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.friendfinder.exceptions.InvalidEmailException;
import com.friendfinder.exceptions.InvalidNameException;
import com.friendfinder.exceptions.InvalidPasswordException;
import jakarta.persistence.*;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_table")
@JsonIdentityInfo(
        scope = User.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;
    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    @ManyToMany
    private List<User> friends;

    @ManyToMany(mappedBy = "participants")
    @JsonIgnore
    private Set<Chat> chats = new HashSet<>();

    // getters and setters
    public Long getUserId() { return userId; }
    public void setUserId(Long id) { this.userId = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<User> getFriends() { return friends; }
    public void setFriends(List<User> friends) { this.friends = friends; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Set<Chat> getChats() { return chats; }
    public void setChats(Set<Chat> chats) { this.chats = chats; }

    public static void isEmailValid(String email) throws InvalidEmailException {
        if (email == null || email.isEmpty()) {
            throw new InvalidEmailException("Email cannot be empty");
        }

        email = email.toLowerCase();

        String emailRegex = "^s\\d{6}@(student\\.)?dtu+\\.dk$";

        if (!email.matches(emailRegex)) {
            throw new InvalidEmailException("Email " + email + " is not valid\n Must be a student DTU mail");
        }
    }

    public static void isPasswordValid(String password) throws InvalidPasswordException {
        if (password == null || password.isEmpty()) {
            throw new InvalidPasswordException("Password cannot be empty");
        }

        if (password.length() < 8) {
            throw new InvalidPasswordException("Password must be at least 8 characters long");
        }
    }

    public static void isNameValid(String name) throws InvalidNameException {
        if (name == null || name.isEmpty()) {
            throw new InvalidNameException("Name cannot be empty");
        }

        if (name.length() < 2) {
            throw new InvalidNameException("Name must be at least 2 characters long");
        }
        if (name.length() > 32) {
            throw new InvalidNameException("Name cannot be longer than 32 characters");
        }

        if (!name.matches("^[A-Za-zÆØÅæøå]+(?: [A-Za-zÆØÅæøå]+)*$")) {
            throw new InvalidNameException("Name must contain only letters and single spaces");
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", chatCount=" + (chats != null ? chats.size() : 0) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId != null && userId.equals(user.getUserId());
    }
}
