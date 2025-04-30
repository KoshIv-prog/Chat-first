package org.example.chat.config;

import org.example.chat.domain.User;
import org.example.chat.domain.UserStatus;
import org.example.chat.service.ChatService;
import org.example.chat.service.MessageService;
import org.example.chat.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;


@EnableScheduling   //to google cloud not to turn off database
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
                userService.addUser("Administrator", new BCryptPasswordEncoder().encode("PASSWORD ADMIN"), UserStatus.ADMIN);
                User user = userService.findUserByUsername("Administrator");
                chatService.addChat("Schedule chat",true,List.of(Administrator),"Administrator");
                messageService.updateDatabase("Administrator");
            }
        };
    }
}
