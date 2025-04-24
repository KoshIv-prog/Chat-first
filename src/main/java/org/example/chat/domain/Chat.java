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
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false, name = "chat_id")
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "chat_users",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private List<User> users;

    @Column(nullable = false, name = "is_group_chat")
    private Boolean isGroupChat;

    @Column(nullable = false, name = "chat_name")
    private String chatName;

    @ManyToOne()
    @JoinColumn(nullable = false, name = "user_admin_id")
    private User userAdmin;

    public Chat(Long id, Boolean isGroupChat,List<User> users, String chatName, User userAdmin) {
        this.id = id;
        this.users = users;
        this.isGroupChat = isGroupChat;
        this.chatName = chatName;
        this.userAdmin = userAdmin;
    }
}
