package org.example.chat.repository;

import org.example.chat.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"chats"})
    Optional<User> findUserByName(String name);

    boolean existsUserByName(String name);

    @EntityGraph(attributePaths = {"chats"})
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :pattern, '%'))")
    List<User> findByPattern(@Param("pattern") String pattern, Pageable pageable);


    @Query(value = "select u.* from chat_users ch inner join user u on ch.user_id = u.user_id where ch.chat_id =:chatId", nativeQuery = true)
    List<User> findUsersByChatId(@Param("chatId") Long chatId);

    @Query("SELECT COUNT(u) FROM User u")
    long countAllUsers();
}
