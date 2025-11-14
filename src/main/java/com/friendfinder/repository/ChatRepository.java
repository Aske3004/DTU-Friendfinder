package com.friendfinder.repository;

import com.friendfinder.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    // find chats by participant user ID
    @Query("SELECT c FROM Chat c JOIN c.participants p WHERE p.userId = :userId")
    List<Chat> findByParticipantsUserId(@Param("userId") Long userId);

    // find chat by chat name
    List<Chat> findByChatName(String chatName);

    // find chat by type
    List<Chat> findByChatType(String chatType);

    // find dm between two users
    @Query("SELECT c FROM Chat c " +
            "JOIN c.participants p1 " +
            "JOIN c.participants p2 " +
            "WHERE c.chatType = 'DIRECT' " +
            "AND p1.userId = :user1Id " +
            "AND p2.userId = :user2Id " +
            "AND SIZE(c.participants) = 2")
    Optional<Chat> findDirectChatBetweenUsers(@Param("user1Id") Long user1Id,
                                              @Param("user2Id") Long user2Id);

    // find all gc's
    @Query("SELECT c FROM Chat c WHERE c.chatType = 'GROUP'")
    List<Chat> findAllGroupChats();

    // find all chats with phrase in name
    @Query("SELECT c FROM Chat c WHERE LOWER(c.chatName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Chat> searchChatsByName(@Param("searchTerm") String searchTerm);

    // number of chats user is in
    @Query("SELECT COUNT(c) FROM Chat c JOIN c.participants p WHERE p.userId = :userId")
    Long countChatsByUserId(@Param("userId") Long userId);

    // find chats where user is a participant ordered by most recent message
    @Query("SELECT DISTINCT c FROM Chat c " +
            "LEFT JOIN c.messages m " +
            "JOIN c.participants p " +
            "WHERE p.userId = :userId " +
            "ORDER BY m.timestamp DESC")
    List<Chat> findUserChatsOrderedByRecentActivity(@Param("userId") Long userId);

}
