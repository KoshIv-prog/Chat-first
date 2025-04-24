package org.example.chat.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false, name = "message_id")
    private Long id;

    @Column(nullable = false, name = "message_text")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false, name = "id_of_message_autor", referencedColumnName = "user_id")
    private User author;

    @Column(nullable = false, updatable = false,name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false,name = "message_in_chat_id",referencedColumnName = "chat_id")
    private Chat chat;

    @Column
    @ElementCollection
    private List<Long> haveNotifiedIds = new ArrayList<>();

    @Column
    @ElementCollection
    private List<Long> hiddenForUserId = new ArrayList<>();

    public Message(Long id, String text, User author, Date date, Chat chat, List<Long> haveNotifiedIds) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.date = date;
        this.chat = chat;
        this.haveNotifiedIds = haveNotifiedIds;
        this.hiddenForUserId = new ArrayList<>();
    }
}
