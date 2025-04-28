package org.example.chat.controller;

import org.example.chat.domain.Chat;
import org.example.chat.dto.ChatDTO;
import org.example.chat.dto.MessageDTO;
import org.example.chat.dto.MessageMethod;
import org.example.chat.dto.UserDTO;
import org.example.chat.dto.response.Response;
import org.example.chat.domain.User;
import org.example.chat.service.ChatService;
import org.example.chat.service.MessageService;
import org.example.chat.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
public class Controller {
    private final Integer SIZE = 30;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final MessageService messageService;
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public Controller(UserService userService,
                      MessageService messageService,
                      ChatService chatService,
                      SimpMessagingTemplate messagingTemplate) {
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.userService = userService;
        this.messageService = messageService;
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/get-user")
    public ResponseEntity<UserDTO> getUser() {
        return ResponseEntity.ok(UserDTO.toDTO(userService.findUserByUsername(getCurrentUser().getUsername())));
    }

    @GetMapping("/get-user-chats")
    public ResponseEntity<List<ChatDTO>> getUserChats(@RequestParam(required = false, defaultValue = "0")int page,
                                                      @RequestParam(required = false, defaultValue = "30")int pageSize) {
        User user = userService.findUserByUsername(getCurrentUser().getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Custom-Header", "MyValue")
                .body(chatService.getChatsForUser(user,page,pageSize));
    }

    @GetMapping("/get-chat-messages")
    public ResponseEntity<List<MessageDTO>> getChatMessages(@RequestParam(required = true) Long chatId,
                                                            @RequestParam(required = false,defaultValue = "0") int page,
                                                            @RequestParam(required = false,defaultValue = "30") int pageSize) {
        List<MessageDTO> messages = new ArrayList<>();

        if (!chatService.existsChat(chatId)){
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Custom-Header","MyValue")
                    .body(new ArrayList<>());
        }

        if(chatService.getChatById(chatId).getUsers().contains(userService.findUserByUsername(getCurrentUser().getUsername()))) {
            messageService.getMessages(chatId,getCurrentUser().getUsername(),page,pageSize)
                    .forEach(message -> {
                        messages.add(MessageDTO.toDTO(message));
                    });
        }

        return ResponseEntity.status(HttpStatus.OK)
                .header("Custom-Header","MyValue")
                .body(messages);
    }

    @PostMapping("/register-new-user")
    public ResponseEntity<Response> registerNewUser(@RequestBody UserDTO userDTO) {
        System.out.println(userDTO.toString());
        return  ResponseEntity.status(HttpStatus.CREATED)
                .header("Custom-Header", "MyValue")
                .body(
                        userService.addUser(userDTO.getName(), passwordEncoder.encode(userDTO.getPassword()))
                );
    }

    @MessageMapping("/send-message")
    public void sendMessage(@Payload MessageDTO messageDTO, Principal principal) {
        if (messageDTO.getMethod() == null){
        }else if (messageDTO.getMethod().equals(MessageMethod.ADD_MESSAGE.toString())) {
            System.out.println("add message");
            MessageDTO newMessage = messageService.addMessage(messageDTO.getChatId(),messageDTO.getText(),principal.getName());
            for (User user : chatService.getAllChatUsers(messageDTO.getChatId())) {
                messagingTemplate.convertAndSend("/topic/messages-for-user-"+user.getName(),newMessage);
            }

        }else if(messageDTO.getMethod().equals(MessageMethod.REMOVE_MESSAGE.toString())){
            System.out.println("remove message");
            Long chatId = messageService.removeMessage(messageDTO.getId(),principal.getName());
            if(chatId != -1L) {
                for (User user : chatService.getAllChatUsers(chatId)) {
                    messageDTO.setMethod(MessageMethod.REMOVE_MESSAGE.toString());
                    messagingTemplate.convertAndSend("/topic/messages-for-user-" + user.getName(), messageDTO);
                }
            }else {
                messageDTO.setMethod(MessageMethod.ACCESS_ERROR.toString());
                messagingTemplate.convertAndSend("/topic/messages-for-user-"+principal.getName(), messageDTO);
            }

        }else if (messageDTO.getMethod().equals(MessageMethod.CHANGE_MESSAGE.toString())){
            System.out.println("change message");
            Long chatId = messageService.editMessage(messageDTO.getId(),principal.getName(),messageDTO.getText());
            if(chatId != -1L) {
                for (User user : chatService.getAllChatUsers(chatId)) {
                    messageDTO.setMethod(MessageMethod.CHANGE_MESSAGE.toString());
                    messagingTemplate.convertAndSend("/topic/messages-for-user-" + user.getName(), messageDTO);
                }
            }else {
                messageDTO.setMethod(MessageMethod.ACCESS_ERROR.toString());
                messagingTemplate.convertAndSend("/topic/messages-for-user-"+principal.getName(), messageDTO);
            }

        }
    }

    @GetMapping("search-users")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String pattern,
                                                     @RequestParam(required = false,defaultValue = "0") Integer page) {
        return ResponseEntity.status(HttpStatus.OK)
                .header("Custom-Header", "MyValue")
                .body(
                        userService.findByPattern(pattern, PageRequest.of(page,SIZE,Sort.Direction.DESC,"id"))
                );
    }

    @PostMapping("/create-new-chat")
    public ResponseEntity<Response> createNewChat(@RequestBody ChatDTO chatDTO) {
        System.out.println(chatDTO.toString());
        return ResponseEntity.status(HttpStatus.OK)
                .header("Custom-Header", "MyValue")
                .body(
                        chatService.addChat(
                            chatDTO.getChatName(),
                            chatDTO.getIsGroupChat(),
                            chatDTO.getUsers(),
                            chatDTO.getUserAdmin()
                        )
                );
    }

    @PostMapping("/hide-message-for-user")
    public ResponseEntity<Response> hideMessageForUser(@RequestBody MessageDTO messageDTO) {
        System.out.println(messageDTO.toString());
        return ResponseEntity.status(HttpStatus.OK)
                .header("Custom-Header", "MyValue")
                .body(
                        messageService.hideMessageForUserById(
                                messageDTO.getId(),
                                messageDTO.getAuthor()

                        )
                );
    }

    @PostMapping("/notify-message")
    public ResponseEntity<Response> notifyMessage(@RequestBody MessageDTO messageDTO) {
        System.out.println(messageDTO.toString()+" to notify");

        Response response = messageService.notifyMessage(messageDTO.getId(),getCurrentUser().getUsername());

        return ResponseEntity.status(HttpStatus.OK)
                .header("Custom-Header", "MyValue")
                .body(response);
    }

    @GetMapping("/get-chat-info")
    public ResponseEntity<ChatDTO> getChatInfo(@RequestParam Long chatId) {

        return ResponseEntity.status(HttpStatus.OK)
                .header("Custom-Header", "MyValue")
                .body(
                        ChatDTO.toDTO(
                                chatService.getChatById(chatId)
                        )
                );
    }

    @PostMapping("/delete-chat")
    public ResponseEntity<Response> deleteChatMethod(@RequestBody ChatDTO chatDTO) {
        System.out.println(chatDTO.getId()+" to delete");
        Response response = chatService.deleteChat(chatDTO.getId(),getCurrentUser().getUsername());

        return ResponseEntity.status(HttpStatus.OK)
                .header("Custom-Header", "MyValue")
                .body(response);
    }

    @PostMapping("/update-chat")
    public ResponseEntity<Response> updateChatMethod(@RequestBody ChatDTO chatDTO) {
        Response response = chatService.updateChat(chatDTO.getId(),chatDTO.getChatName(),chatDTO.getUsers(),getCurrentUser().getUsername());

        return ResponseEntity.status(HttpStatus.OK)
                .header("Custom-Header", "MyValue")
                .body(response);
    }


    private org.springframework.security.core.userdetails.User getCurrentUser() {
        return (org.springframework.security.core.userdetails.User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
