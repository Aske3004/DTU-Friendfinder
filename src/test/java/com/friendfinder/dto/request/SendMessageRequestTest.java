package com.friendfinder.dto.request;

import com.friendfinder.dto.MessageDTO;
import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SendMessageRequestTest {

    private final Validator validator;

    public SendMessageRequestTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validRequest_passesValidation() {
        SendMessageRequest request = new SendMessageRequest();
        request.setContent("Hello, world!");
        request.setType(MessageDTO.MessageType.CHAT);

        Set<ConstraintViolation<SendMessageRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void defaultConstructor_setsChatType() {
        SendMessageRequest request = new SendMessageRequest();
        request.setContent("Test");

        assertEquals(MessageDTO.MessageType.CHAT, request.getType());
    }

    @Test
    void contentConstructor_setsContentAndDefaultType() {
        SendMessageRequest request = new SendMessageRequest("Hello");

        assertEquals("Hello", request.getContent());
        assertEquals(MessageDTO.MessageType.CHAT, request.getType());
    }

    @Test
    void fullConstructor_setsContentAndType() {
        SendMessageRequest request = new SendMessageRequest("System message", MessageDTO.MessageType.SYSTEM);

        assertEquals("System message", request.getContent());
        assertEquals(MessageDTO.MessageType.SYSTEM, request.getType());
    }

    @Test
    void blankContent_failsValidation() {
        SendMessageRequest request = new SendMessageRequest();
        request.setContent("");

        Set<ConstraintViolation<SendMessageRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Message content is required")));
    }

    @Test
    void nullContent_failsValidation() {
        SendMessageRequest request = new SendMessageRequest();
        request.setContent(null);

        Set<ConstraintViolation<SendMessageRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void contentTooLong_failsValidation() {
        SendMessageRequest request = new SendMessageRequest();
        request.setContent("A".repeat(5001));

        Set<ConstraintViolation<SendMessageRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Message content must not exceed 5000 characters")));
    }

    @Test
    void maxLengthContent_passesValidation() {
        SendMessageRequest request = new SendMessageRequest();
        request.setContent("A".repeat(5000));

        Set<ConstraintViolation<SendMessageRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void toString_containsRelevantInfo() {
        SendMessageRequest request = new SendMessageRequest("Hello", MessageDTO.MessageType.CHAT);
        String str = request.toString();

        assertTrue(str.contains("Hello"));
        assertTrue(str.contains("CHAT"));
    }
}
