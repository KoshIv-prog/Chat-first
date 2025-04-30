package org.example.chat.service;

import javax.transaction.Transactional;

import org.example.chat.domain.Chat;
import org.example.chat.domain.User;
import org.example.chat.domain.UserStatus;
import org.example.chat.dto.MessageDTO;
import org.example.chat.domain.Message;
import org.example.chat.dto.MessageMethod;
import org.example.chat.dto.response.Response;
import org.example.chat.repository.ChatRepository;
import org.example.chat.repository.MessageRepository;
import org.example.chat.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository, ChatRepository chatRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
    }

    @Transactional
    public Page<Message> getMessages(Long chatId, String username, int page, int size) {
        if(!userRepository.existsUserByName(username)){
            return Page.empty();
        }

        int correctPage = (int) Math.ceil( messageRepository.countMessagesByChat_Id(chatId)/size)- page;

        if (correctPage<0){
            return Page.empty();
        }

        Long userId = userRepository.findUserByName(username).get().getId();
        notifyMessages(chatId,userId);
        return messageRepository.findVisibleMessagesByChatIdAndUserId(chatId,userId, PageRequest.of(correctPage, size, Sort.Direction.DESC, "id"));
    }


    @Transactional
    public MessageDTO addMessage(Long chatID, String text, String author) {
        if(!chatRepository.existsById(chatID)||!userRepository.existsUserByName(author)) {
            MessageDTO msg = new MessageDTO();
            msg.setMethod(MessageMethod.ACCESS_ERROR.toString());
            return msg ;
        }

        User user = userRepository.findUserByName(author).get();
        Message message = new Message(null ,text, user,new Date(),chatRepository.findChatById(chatID), new ArrayList<>());

        Long msgId = messageRepository.save(message).getId();
        MessageDTO newMsg = MessageDTO.toDTO(message);
        newMsg.setId(msgId);
        newMsg.setMethod(MessageMethod.ADD_MESSAGE.toString());
        return newMsg;
    }

    @Transactional
    public Long removeMessage(Long msgId, String userName) {
        Message message = messageRepository.findMessageById(msgId).get();
        User user = userRepository.findUserByName(userName).orElseGet(null);
        if(user==null) {
            return -1L;
        }

        if (message.getAuthor() == user || user.getStatus() == UserStatus.ADMIN|| message.getChat().getUserAdmin().equals(user)) {
            Long result = message.getChat().getId();
            messageRepository.deleteById(msgId);
            return result;
        }else {
            return -1L;
        }
    }

    @Transactional
    public Long editMessage(Long msgId, String userName, String text) {
        Message message = messageRepository.findMessageById(msgId).get();
        User user = userRepository.findUserByName(userName).orElseGet(null);
        if(user==null) {
            return -1L;
        }

        if (message.getAuthor().equals(user) || user.getStatus() == UserStatus.ADMIN || message.getChat().getUserAdmin().equals(user)) {
            Long result = message.getChat().getId();
            message.setText(text);
            messageRepository.save(message);
            return result;
        }else {
            return -1L;
        }

    }

    @Transactional
    public Response hideMessageForUserById(Long messageId, String userName) {
        Optional<Message> msg = messageRepository.findMessageById(messageId);
        Optional<User> user = userRepository.findUserByName(userName);

        if(msg.isEmpty() || user.isEmpty()) {
            return Response.ERROR;
        }

        msg.get().getHiddenForUserId().add(user.get().getId());
        return Response.OK;
    }


    @Transactional
    public Long countNewMessages(Long chatId, String username) {
        Optional<User> user = userRepository.findUserByName(username);
        if(user.isEmpty()) {
            return -1L;
        }

        return messageRepository.countNewMessages(chatId,user.get().getId());
    }

    @Transactional
    public void notifyMessages(Long chatId, Long userId) {
        List<Message> notNotifiedMessages = messageRepository.findUnreadMessages(chatId,userId);
        notNotifiedMessages.forEach(message -> {
            //if (!message.getHaveNotifiedIds().contains(userId)) {
                message.getHaveNotifiedIds().add(userId);
            //}
        }
        );
        messageRepository.saveAll(notNotifiedMessages);
    }

    @Transactional
    public Response notifyMessage(Long messageId, String username) {
        Optional<User> user = userRepository.findUserByName(username);
        if(user.isEmpty()) {
            System.out.println("Log:  unnotified message  user is empty");
            return Response.ERROR ;
        }
        Optional<Message> message = messageRepository.findMessageById(messageId);
        if(message.isEmpty()) {
            System.out.println("Log:  unnotified message  message is empty");
            return Response.ERROR ;
        }
        Message gotMessage = message.get();
        if(gotMessage.getHaveNotifiedIds().contains(user.get().getId())){
            System.out.println("Log:  unnotified message   is already notified");
            return Response.ERROR;
        }
        System.out.println("Log:  notified message");
        gotMessage.getHaveNotifiedIds().add(user.get().getId());
        messageRepository.save(gotMessage);
        return Response.OK;
    }
}
