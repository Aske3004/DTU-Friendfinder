package com.friendfinder.exceptions;

public class DuplicateReadReceiptException extends RuntimeException {
    public DuplicateReadReceiptException(String message) {
        super(message);
    }
}
