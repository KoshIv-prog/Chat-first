package org.example.chat.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.example.chat.domain.Chat;
import org.example.chat.domain.User;
import org.example.chat.domain.UserStatus;


import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class UserDTO {
    private Long id;

    private String name;

    private String password;


    private List<Chat> chats;

    private UserStatus status;

    public UserDTO(Long id ,String name, String password, List<Chat> chats, UserStatus status) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.chats = chats;
        this.status = status;
    }

    public static UserDTO toDTO(Long id, String name, String password, List<Chat> chats, UserStatus status) {
        return new UserDTO(id,name,password,chats,status);
    }

    public static UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getPassword(), new ArrayList<>(), user.getStatus());
    }

    public static User fromDTO(UserDTO userDTO) {
        return new User(userDTO.getId(), userDTO.getName(), null, userDTO.getChats(), userDTO.getStatus());
    }
}
