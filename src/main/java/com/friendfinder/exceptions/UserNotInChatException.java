package com.friendfinder.exceptions;

public class UserNotInChatException extends RuntimeException {
    public UserNotInChatException(String message) {
        super(message);
    }
}
