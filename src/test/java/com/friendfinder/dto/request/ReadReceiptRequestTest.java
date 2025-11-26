package com.friendfinder.dto.request;

import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ReadReceiptRequestTest {

    private final Validator validator;

    public ReadReceiptRequestTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void validRequest_passesValidation() {
        ReadReceiptRequest request = new ReadReceiptRequest(123L, 456L);

        Set<ConstraintViolation<ReadReceiptRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void nullMessageID_failsValidation() {
        ReadReceiptRequest request = new ReadReceiptRequest();
        request.setMessageID(null);
        request.setUserID(1L);

        Set<ConstraintViolation<ReadReceiptRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Message ID is required")));
    }

    @Test
    void nullUserID_failsValidation() {
        ReadReceiptRequest request = new ReadReceiptRequest();
        request.setMessageID(1L);
        request.setUserID(null);

        Set<ConstraintViolation<ReadReceiptRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("User ID is required")));
    }

    @Test
    void gettersAndSetters_work() {
        ReadReceiptRequest request = new ReadReceiptRequest();
        request.setMessageID(100L);
        request.setUserID(200L);

        assertEquals(100L, request.getMessageID());
        assertEquals(200L, request.getUserID());
    }

    @Test
    void toString_containsIDs() {
        ReadReceiptRequest request = new ReadReceiptRequest(123L, 456L);
        String str = request.toString();

        assertTrue(str.contains("123"));
        assertTrue(str.contains("456"));
    }
}
