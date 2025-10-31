package com.friendfinder.utils;

public record Field(String type, String name, String description, String value, String message) {

    public boolean isValid() {
        return this.value != null && this.message == null;
    }

    public boolean isInvalid() {
        return this.message != null && !this.message.isEmpty();
    }
}
