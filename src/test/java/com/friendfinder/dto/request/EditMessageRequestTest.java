package com.friendfinder.dto.request;

import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class EditMessageRequestTest {

    private final Validator vaidator;

    public EditMessageRequestTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.vaidator = factory.getValidator();
    }

    @Test
    void validRequest_passesValidation() {
        EditMessageRequest request = new EditMessageRequest("Updated content");

        Set<ConstraintViolation<EditMessageRequest>> violations = vaidator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void blankContent_failsValidation() {
        EditMessageRequest request = new EditMessageRequest("");

        Set<ConstraintViolation<EditMessageRequest>> violations = vaidator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void nullContent_failsValidation() {
        EditMessageRequest request = new EditMessageRequest();
        request.setContent(null);

        Set<ConstraintViolation<EditMessageRequest>> violations = vaidator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void contentTooLong_failsValidation() {
        EditMessageRequest request = new EditMessageRequest("A".repeat(5001));

        Set<ConstraintViolation<EditMessageRequest>> violations = vaidator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void gettersAndSetters_work() {
        EditMessageRequest request = new EditMessageRequest();
        request.setContent("content");

        assertEquals("content", request.getContent());
    }

    @Test
    void toString_containsContent() {
        EditMessageRequest request = new EditMessageRequest("Test");
        assertTrue(request.toString().contains("Test"));
    }
}
