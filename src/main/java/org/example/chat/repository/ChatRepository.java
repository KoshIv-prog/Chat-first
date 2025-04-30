package org.example.chat.repository;

import org.example.chat.domain.Chat;
import org.example.chat.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @EntityGraph(attributePaths = {"users", "userAdmin"})
    Chat findChatById(Long id);

    @EntityGraph(attributePaths = {"users", "userAdmin"})
    List<Chat> findAllByUsersContaining(User user, PageRequest pageable);


}
