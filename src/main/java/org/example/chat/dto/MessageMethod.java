package org.example.chat.dto;

public enum MessageMethod {
    ADD_MESSAGE,REMOVE_MESSAGE,CHANGE_MESSAGE,ACCESS_ERROR;

    @Override
    public String toString() {
        return this.name();
    }
}
