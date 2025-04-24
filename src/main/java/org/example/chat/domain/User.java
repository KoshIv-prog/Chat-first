package org.example.chat.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false, name = "user_id")
    private Long id;

    @Column(unique = true, nullable = false, name = "user_name")
    private String name;

    @Column(nullable = false,name = "user_password")
    private String password;

    @JsonIgnore
    @ManyToMany(mappedBy = "users")
    private List<Chat> chats;

    @Column(nullable = false)
    private UserStatus status;

    public User(Long id, String name, String password, List<Chat> chats, UserStatus status) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.chats = chats;
        this.status = status;
    }
}
