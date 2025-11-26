package com.friendfinder.dto.request;

import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class JoinChatRequestTest {

    private final Validator validator;

    public JoinChatRequestTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void validRequest_passesValidation() {
        JoinChatRequest request = new JoinChatRequest(1L, 2L, "Alice");

        Set<ConstraintViolation<JoinChatRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void nullChatID_failsValidation() {
        JoinChatRequest request = new JoinChatRequest(null, 1L, "Alice");

        Set<ConstraintViolation<JoinChatRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Chat ID is required")));
    }

    @Test
    void nullUserID_failsValidation() {
        JoinChatRequest request = new JoinChatRequest(1L, null, "Alice");

        Set<ConstraintViolation<JoinChatRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("User ID is required")));
    }

    @Test
    void blankUsername_failsValidation() {
        JoinChatRequest request = new JoinChatRequest(1L, 2L, "");

        Set<ConstraintViolation<JoinChatRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Username is required")));
    }

    @Test
    void nullUsername_failsValidation() {
        JoinChatRequest request = new JoinChatRequest(1L, 2L, null);

        Set<ConstraintViolation<JoinChatRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void gettersAndSetters_work() {
        JoinChatRequest request = new JoinChatRequest();
        request.setChatID(10L);
        request.setUserID(20L);
        request.setUsername("Bob");

        assertEquals(10L, request.getChatID());
        assertEquals(20L, request.getUserID());
        assertEquals("Bob", request.getUsername());
    }

    @Test
    void toString_containsAllFields() {
        JoinChatRequest request = new JoinChatRequest(1L, 2L, "Bob");
        String str = request.toString();

        assertTrue(str.contains("1"));
        assertTrue(str.contains("2"));
        assertTrue(str.contains("Bob"));
    }
}
