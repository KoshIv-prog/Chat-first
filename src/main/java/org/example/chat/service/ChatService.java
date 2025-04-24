package org.example.chat.service;

import javax.transaction.Transactional;

import org.example.chat.dto.ChatDTO;
import org.example.chat.dto.response.Response;
import org.example.chat.domain.Chat;
import org.example.chat.domain.User;
import org.example.chat.repository.ChatRepository;
import org.example.chat.repository.UserRepository;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final MessageService messageService;

    public ChatService(ChatRepository chatRepository, UserRepository userRepository, MessageService messageService) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.messageService = messageService;
    }

    @Transactional
    public Response addChat(String chatName, Boolean isGroupChat, List<String> users, String adminUserName) {
        if (users.isEmpty()) {
            return Response.ERROR;
        }
        if (!isGroupChat && users.size() != 2) {
            return Response.ERROR;
        }
        Chat chat = new Chat(null,isGroupChat, new ArrayList<User>() ,chatName,null);
        Optional<User> userAdmin =userRepository.findUserByName(adminUserName);

        if (userAdmin.isEmpty()) {
            return Response.ERROR;
        }
        chat.setUserAdmin(userAdmin.get());

        Optional<User> user;
        for (String string : users) {
            user = userRepository.findUserByName(string);
            user.ifPresent(value -> chat.getUsers().add(value));
        }

        chatRepository.save(chat);
        return Response.OK;
    }

    @Transactional
    public void addUsersToChat(List<String> userNames, Long chatId) {
        for (String userName : userNames) {
            Optional<User> user = userRepository.findUserByName(userName);
            Optional<Chat> chat = chatRepository.findById(chatId);
            if (user.isPresent() || chat.isPresent()) {
                chat.get().getUsers().add(user.get());
                chatRepository.save(chat.get());
            }
        }
    }

    @Transactional
    @ReadOnlyProperty
    public List<User> getAllChatUsers(Long chatId){
        return userRepository.findUsersByChatId(chatId);
    }

    @ReadOnlyProperty
    @Transactional
    public Chat getChatById(Long chatId) {
        return chatRepository.findById(chatId).orElseGet(Chat::new);
    }

    @Transactional
    public List<ChatDTO> getChatsForUser(User user) {
        List<ChatDTO> chatDTOS = new ArrayList<ChatDTO>();
        ChatDTO chatDTO;
        for (Chat chat : chatRepository.findAllByUsersContaining(user)) {
            chatDTO =ChatDTO.toDTO(chat);
            chatDTO.setUnNotified(messageService.countNewMessages(chat.getId(),user.getName()));
            chatDTOS.add(chatDTO);
        };
        return chatDTOS;
    }

    @Transactional
    public boolean existsChat(Long chatId) {
        return chatRepository.existsById(chatId);
    }

}
