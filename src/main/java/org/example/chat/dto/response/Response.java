package org.example.chat.dto.response;

public enum Response {
    OK, LOGIN_ERROR, REGISTER_ERROR,ERROR,ACCESS_ERROR;

    @Override
    public String toString() {
        return "STATUS_" + this.name();
    }
}
