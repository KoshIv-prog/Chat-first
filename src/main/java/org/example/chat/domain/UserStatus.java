package org.example.chat.domain;

public enum UserStatus {
    USER, ADMIN;

    @Override
    public String toString() {
        return "ROLE_" + this.name();
    }
}