package org.example.chat.config;

import org.example.chat.service.ChatService;
import org.example.chat.service.MessageService;
import org.example.chat.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AppConfig extends GlobalMethodSecurityConfiguration {
    @Bean
    public CommandLineRunner demo(final UserService userService,
                                  final MessageService messageService,
                                  final ChatService chatService) {
        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {
                userService.addUser("qwe",new BCryptPasswordEncoder().encode("qwe"));
                userService.addUser("qw",new BCryptPasswordEncoder().encode("qw"));
                List<String> users = new ArrayList<>();
                users.add("qwe");
                users.add("qw");
                chatService.addChat("qwe123",false,users,"qwe");
                messageService.addMessage(3L,"hello","qwe");
                messageService.addMessage(3L,"hello","qw");
                messageService.addMessage(3L,"hello","qw");
                for (int i = 0; i < 100; i++){
                    messageService.addMessage(3L,"hello_"+i,"qwe");
                }
                for (int i = 0; i < 100; i++){
                    chatService.addChat("chat_"+i,true,users,"qwe");
                }
                messageService.hideMessageForUserById(6L,"qwe");
            }
        };
    }
}
