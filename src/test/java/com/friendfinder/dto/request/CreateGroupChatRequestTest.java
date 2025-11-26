package com.friendfinder.dto.request;

import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CreateGroupChatRequestTest {

    private final Validator validator;

    public CreateGroupChatRequestTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validRequest_passesValidation() {
        CreateGroupChatRequest request = new CreateGroupChatRequest();
        request.setChatName("Study Group");
        request.setCreatorID(1L);
        request.setParticipantIDs(Set.of(2L, 3L, 4L));

        Set<ConstraintViolation<CreateGroupChatRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void blankChatName_failsValidation() {
        CreateGroupChatRequest request = new CreateGroupChatRequest();
        request.setChatName("");
        request.setCreatorID(1L);
        request.setParticipantIDs(Set.of(2L));

        Set<ConstraintViolation<CreateGroupChatRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Chat name is required")));
    }

    @Test
    void nullChatName_failsValidation() {
        CreateGroupChatRequest request = new CreateGroupChatRequest();
        request.setChatName(null);
        request.setCreatorID(1L);
        request.setParticipantIDs(Set.of(2L));

        Set<ConstraintViolation<CreateGroupChatRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void nullCreatorID_failsValidation() {
        CreateGroupChatRequest request = new CreateGroupChatRequest();
        request.setChatName("Study Group");
        request.setCreatorID(null);
        request.setParticipantIDs(Set.of(2L));

        Set<ConstraintViolation<CreateGroupChatRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Creator ID is required")));
    }

    @Test
    void emptyParticipantIDs_failsValidation(){
        CreateGroupChatRequest request = new CreateGroupChatRequest();
        request.setChatName("Study Group");
        request.setCreatorID(1L);
        request.setParticipantIDs(new HashSet<>());

        Set<ConstraintViolation<CreateGroupChatRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("At least one participant is required")));
    }

    @Test
    void chatNameTooLong_failsValidation(){
        CreateGroupChatRequest request = new CreateGroupChatRequest();
        request.setChatName("A".repeat(101));
        request.setCreatorID(1L);
        request.setParticipantIDs(Set.of(2L));

        Set<ConstraintViolation<CreateGroupChatRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Chat name must not exceed 100 characters")));
    }

    @Test
    void fullConstructor_setsAllFields() {
        Set<Long> participants = Set.of(2L, 3L, 4L);
        CreateGroupChatRequest request = new CreateGroupChatRequest("My Chat", 1L, participants);

        assertEquals("My Chat", request.getChatName());
        assertEquals(1L, request.getCreatorID());
        assertEquals(participants, request.getParticipantIDs());
    }

    @Test
    void toString_containsRelevantInfo() {
        CreateGroupChatRequest request = new CreateGroupChatRequest();
        request.setChatName("Study Group");
        request.setCreatorID(1L);
        request.setParticipantIDs(Set.of(2L, 3L));

        String str = request.toString();
        assertTrue(str.contains("Study Group"));
        assertTrue(str.contains("1"));
    }
}
