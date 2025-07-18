package org.example.chat.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserService userService;

    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        org.example.chat.domain.User customUser = userService.findUserByUsername(username);
        if (customUser == null) {
            throw new UsernameNotFoundException(username+" not found");
        }

        List<GrantedAuthority> roles = Arrays.asList(
                new SimpleGrantedAuthority(customUser.getStatus().toString())
        );
        return new User(customUser.getName(), customUser.getPassword(), roles);
    }
}
