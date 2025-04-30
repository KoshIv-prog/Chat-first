package org.example.chat.service;


import org.example.chat.dto.UserDTO;
import org.example.chat.dto.response.Response;
import org.example.chat.domain.User;
import org.example.chat.domain.UserStatus;
import org.example.chat.repository.UserRepository;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public Response addUser(String username, String password, UserStatus status) {
        System.out.println("Log:  registering user");
        if (userRepository.existsUserByName(username)) {
            System.out.println("Log:  user already exists");
            return Response.REGISTER_ERROR;
        }

        userRepository.save(new User(null, username, password, new ArrayList<>(), status));
        return Response.OK;
    }

    @Transactional()
    @ReadOnlyProperty()
    public List<UserDTO> findByPattern(String pattern, Pageable pageable) {
        List<UserDTO> usersDTO = new ArrayList<>();
        List<User> users = userRepository.findByPattern(pattern, pageable);
        for (int i = users.size()-1; 0 <= i; i--) {
            usersDTO.add(UserDTO.toDTO(users.get(i)));
        }

        return usersDTO;
    }

    public User findUserByUsername(String username) {
        return userRepository.findUserByName(username).orElse(null);
    }
}
