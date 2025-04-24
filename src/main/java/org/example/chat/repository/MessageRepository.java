package org.example.chat.repository;

import org.example.chat.domain.Message;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @EntityGraph(attributePaths = {"author", "chat"})
    @Query("""
    SELECT m FROM Message m
    WHERE m.chat.id = :chatId
    AND NOT EXISTS (
        SELECT 1 FROM Message m2 JOIN m2.hiddenForUserId hiddenUserId
        WHERE m2 = m AND hiddenUserId = :userId
    )
    ORDER BY m.date ASC
""")
    List<Message> findVisibleMessagesByChatIdAndUserId(@Param("chatId") Long chatId, @Param("userId") Long userId,PageRequest pageable);

    @EntityGraph(attributePaths = {"author", "chat"})
    Optional<Message> findMessageById(Long id);


    @Query("""
            SELECT COUNT(m) FROM Message m 
            WHERE m.chat.id = :chatId 
            AND :userId NOT IN elements(m.haveNotifiedIds)
            
                        """)
    Long countNewMessages(@Param("chatId") Long chatId, @Param("userId") Long userId);


    @Query("""
            SELECT m FROM Message m 
            WHERE m.chat.id = :chatId 
            AND m.author.id <> :userId 
            AND :userId NOT IN elements(m.haveNotifiedIds)
                    """)
    List<Message> findUnreadMessages(@Param("chatId") Long chatId, @Param("userId") Long userId);
}




