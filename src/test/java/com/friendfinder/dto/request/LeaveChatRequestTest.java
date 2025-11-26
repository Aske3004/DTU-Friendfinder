package com.friendfinder.dto.request;

import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class LeaveChatRequestTest {

    private final Validator validator;

    public LeaveChatRequestTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void validRequest_passesValidation() {
        LeaveChatRequest request = new LeaveChatRequest(1L, 2L, "Alice");

        Set<ConstraintViolation<LeaveChatRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void nullChatID_failsValidation() {
        LeaveChatRequest request = new LeaveChatRequest(null, 1L, "Alice");

        Set<ConstraintViolation<LeaveChatRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Chat ID is required")));
    }

    @Test
    void nullUserID_failsValidation() {
        LeaveChatRequest request = new LeaveChatRequest(1L, null, "Alice");

        Set<ConstraintViolation<LeaveChatRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void blankUsername_failsValidation() {
        LeaveChatRequest request = new LeaveChatRequest(1L, 2L, "");

        Set<ConstraintViolation<LeaveChatRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void gettersAndSetters_work() {
        LeaveChatRequest request = new LeaveChatRequest();
        request.setChatID(100L);
        request.setUserID(200L);
        request.setUsername("Bob");

        assertEquals(100L, request.getChatID());
        assertEquals(200L, request.getUserID());
        assertEquals("Bob", request.getUsername());
    }

    @Test
    void toString_containsAllFields() {
        LeaveChatRequest request = new LeaveChatRequest(1L, 2L, "Alice");
        String str = request.toString();

        assertTrue(str.contains("1"));
        assertTrue(str.contains("2"));
        assertTrue(str.contains("Alice"));
    }

}
