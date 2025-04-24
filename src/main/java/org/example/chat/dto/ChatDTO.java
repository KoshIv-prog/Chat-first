package org.example.chat.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.chat.domain.Chat;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class ChatDTO {
    private Long id;
    private List<String> users;
    private Boolean isGroupChat;
    private String chatName;
    private String userAdmin;
    private Long unNotified;

    public ChatDTO(Long id, List<String> users, Boolean isGroupChat, String chatName, String userAdmin) {
        this.id = id;
        this.users = users;
        this.isGroupChat = isGroupChat;
        this.chatName = chatName;
        this.userAdmin = userAdmin;
    }

    public static ChatDTO toDTO(Long id, List<String> users, Boolean isGroupChat, String chatName, String userAdmin) {
        return new ChatDTO(id, users, isGroupChat, chatName, userAdmin);
    }

    public static ChatDTO toDTO(Chat chat) {
        List <String> userNames = new ArrayList<>();
        chat.getUsers().forEach(user -> userNames.add(user.getName()));
        return new ChatDTO(chat.getId(), userNames , chat.getIsGroupChat(), chat.getChatName(),chat.getUserAdmin().getName());
    }

    /*public static Chat fromDTO (ChatDTO chatDTO){
        return new Chat(chatDTO.getId(), chatDTO.getIsGroupChat(), chatDTO.getUsers(), chatDTO.getChatName(), chatDTO.getUserAdmin());
    }*/
}
