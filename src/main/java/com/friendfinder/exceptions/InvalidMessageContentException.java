package com.friendfinder.exceptions;

public class InvalidMessageContentException extends RuntimeException {
    public InvalidMessageContentException(String message) {
        super(message);
    }
}
