package org.example.chat.service;

import javax.transaction.Transactional;

import org.example.chat.domain.UserStatus;
import org.example.chat.dto.ChatDTO;
import org.example.chat.dto.response.Response;
import org.example.chat.domain.Chat;
import org.example.chat.domain.User;
import org.example.chat.repository.ChatRepository;
import org.example.chat.repository.MessageRepository;
import org.example.chat.repository.UserRepository;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final MessageService messageService;
    private final MessageRepository messageRepository;

    public ChatService(ChatRepository chatRepository, UserRepository userRepository, MessageService messageService, MessageRepository messageRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.messageService = messageService;
        this.messageRepository = messageRepository;
    }

    @Transactional
    public Response addChat(String chatName, Boolean isGroupChat, List<String> users, String adminUserName) {
        if (users.isEmpty()|| chatName.length() > 25) {
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
    public List<ChatDTO> getChatsForUser(User user,int page,int size) {
        List<ChatDTO> chatDTOS = new ArrayList<ChatDTO>();
        ChatDTO chatDTO;
        for (Chat chat : chatRepository.findAllByUsersContaining(user, PageRequest.of(page, size, Sort.Direction.DESC, "id"))) {
            chatDTO =ChatDTO.toDTO(chat);
            chatDTO.setUnNotified(messageService.countNewMessages(chat.getId(),user.getName()));
            chatDTOS.add(chatDTO);
        };
        return chatDTOS;
    }

    @Transactional
    public Response deleteChat(Long chatId,String userName) {
        Optional<Chat> chatOptional = chatRepository.findById(chatId);
        if (chatOptional.isEmpty()) {
            return Response.ERROR;
        }
        Chat chat = chatOptional.get();
        if(!chat.getUserAdmin().getName().equals(userName)){
            return Response.ACCESS_ERROR;
        }
        messageRepository.deleteAllByChat(chat);
        chatRepository.delete(chat);

        return Response.OK;
    }

    @Transactional
    public boolean existsChat(Long chatId) {
        return chatRepository.existsById(chatId);
    }

    @Transactional
    public Response updateChat(Long chatId,String chatName,List<String> users,String userName) {
        Optional<Chat> optionalChat = chatRepository.findById(chatId);
        if (optionalChat.isEmpty()|| users.isEmpty()|| chatName.length()>25) {
            return Response.ERROR;
        }
        Chat chat = optionalChat.get();

        Optional<User> user = userRepository.findUserByName(userName);
        if (user.isEmpty()){
            return Response.ERROR;
        }
        if(chat.getUserAdmin()!=user.get()){
            if (user.get().getStatus()!= UserStatus.ADMIN) {
                return Response.ACCESS_ERROR;
            }
        }

        if(!chat.getIsGroupChat()){
            return Response.ERROR;
        }

        chat.setChatName(chatName);
        chat.setUsers(new ArrayList<>());
        users.forEach(userS -> {
            Optional<User> userOptional = userRepository.findUserByName(userS);
            if (userOptional.isPresent()){
                chat.getUsers().add(userOptional.get());
            }
        });
        chatRepository.save(chat);

        return Response.OK;
    }

}
