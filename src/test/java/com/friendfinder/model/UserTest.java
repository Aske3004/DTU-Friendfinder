package com.friendfinder.model;

import com.friendfinder.exceptions.InvalidEmailException;
import com.friendfinder.exceptions.InvalidNameException;
import com.friendfinder.exceptions.InvalidPasswordException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void validEmail_shouldNotThrowException() {
        assertDoesNotThrow(() -> User.isEmailValid("s123456@student.dtu.dk"));
    }
    @Test
    void invalidEmail_shouldThrowException() {
        assertThrows(InvalidEmailException.class, () -> User.isEmailValid("notAValidEmail"));
    }
    @Test
    void shortPassword_shouldThrowException() {
        assertThrows(InvalidPasswordException.class, () -> User.isPasswordValid("short"));
    }
    @Test
    void validPassword_shouldNotThrowException() {
        assertDoesNotThrow(() -> User.isPasswordValid("strongPass123"));
    }
    @Test
    void emptyName_shouldThrowException() {
        assertThrows(InvalidNameException.class, () -> User.isNameValid(""));
    }
    @Test
    void validName_shouldNotThrowException() {
        assertDoesNotThrow(() -> User.isNameValid("Bob"));
    }
    @Test
    void nameWithNumbers_shouldThrowException() {
        assertThrows(InvalidNameException.class, () -> User.isNameValid("Bob123"));
    }
    @Test
    void testGettersAndSetters() {
        User user = new User();
        user.setId(1);
        user.setName("Bob");
        user.setEmail("s123456@student.dtu.dk");
        user.setPassword("password123");

        assertEquals(1, user.getId());
        assertEquals("Bob", user.getName());
        assertEquals("s123456@student.dtu.dk", user.getEmail());
        assertEquals("password123", user.getPassword());
    }

}
