package org.example.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.chat.domain.Message;
import org.example.chat.service.ChatService;
import org.example.chat.service.UserService;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class MessageDTO {
    private Long id;

    private String text;

    private String author;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm", timezone = "UTC")
    private Date date;

    private Long chatId;

    private List<Long> haveNotifiedIds;

    private String method;

    public MessageDTO(Long id ,String text, String author, Date date, Long chat, List<Long> haveNotifiedIds, String method) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.date = date;
        this.chatId = chat;
        this.haveNotifiedIds = haveNotifiedIds;
        this.method = method;
    }
    public MessageDTO() {

    }

    public static MessageDTO toDTO(Long id ,String text, String author, Date date, Long chat, List<Long> haveNotifiedIds, String method) {
        return new MessageDTO(id, text, author, date, chat, haveNotifiedIds,method);
    }

    public static MessageDTO toDTO(Message message) {
        MessageDTO messageDTO = new MessageDTO(message.getId(), message.getText(), message.getAuthor().getName(), message.getDate(), message.getChat().getId(), message.getHaveNotifiedIds(),null);
        return messageDTO;
    }

    public static Message fromDTO(MessageDTO messageDTO, ChatService chatService, UserService userService) {
        return new Message(messageDTO.getId(), messageDTO.getText(), userService.findUserByUsername(messageDTO.getAuthor()), messageDTO.getDate(), chatService.getChatById(messageDTO.getChatId()), messageDTO.getHaveNotifiedIds());
    }
}
