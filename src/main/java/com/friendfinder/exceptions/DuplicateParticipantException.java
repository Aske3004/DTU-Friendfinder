package com.friendfinder.exceptions;

public class DuplicateParticipantException extends RuntimeException {
    public DuplicateParticipantException(String message) {
        super(message);
    }
}
