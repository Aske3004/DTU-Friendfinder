package com.friendfinder.exceptions;

public class ReadReceiptNotFoundException extends RuntimeException {
    public ReadReceiptNotFoundException(String message) {
        super(message);
    }
}
