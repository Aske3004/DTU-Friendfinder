package com.friendfinder.repository;

import com.friendfinder.model.Chat;
import com.friendfinder.model.Message;
import com.friendfinder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // find all messages in a chat newest first
    List<Message> findByChatOrderByTimestampDesc(Chat chat);

    // reverse order
    List<Message> findByChatOrderByTimestampAsc(Chat chat);

    // find newest message in chat
    Message findTopByChatOrderByTimestampDesc(Chat chat);

    // find messages not read my specific user in a chat
    @Query("SELECT m FROM Message m WHERE m.chat = :chat " +
    "AND m.sender != :user " +
    "AND NOT EXISTS (SELECT r FROM MessageReadReceipt r WHERE r.message = m AND r.user = :user)")
    List<Message> findUnreadMessagesByUserInChat(@Param("chat") Chat chat,
                                                 @Param("user") User user);


    // count unread messages in chat for specific user
    @Query("SELECT COUNT(m) FROM Message m WHERE m.chat = :chat " +
    "AND m.sender != :user " +
    "AND NOT EXISTS (SELECT r FROM MessageReadReceipt r WHERE r.message = m AND r.user = :user)")
    int countUnreadMessagesByUserInChat(@Param("chat") Chat chat,
                                        @Param("user") User user);

    // find all messages sent by a specific user
    List<Message> findBySender(User sender);

    // find messages by type
    List<Message> findByMessageType(String messageType);

    // find messages by type And Chat
    List<Message> findByChatAndMessageType(Chat chat, String messageType);

    // find messages sent after a specific time in chat
    @Query("SELECT m FROM Message m WHERE m.chat = :chat AND m.timestamp > :after ORDER BY m.timestamp ASC")
    List<Message> findMessagesSentAfter(@Param("chat") Chat chat,
                                        @Param("after") LocalDateTime after);

    // find messages sent before a specific time in chat
    @Query("SELECT m FROM Message m WHERE m.chat = :chat AND m.timestamp < :before ORDER BY m.timestamp DESC")
    List<Message> findMessagesSentBefore(@Param("chat") Chat chat,
                                         @Param("before") LocalDateTime before);

    // find edited messages in chat
    @Query("SELECT m FROM Message m WHERE m.chat = :chat AND m.isEdited = true ORDER BY m.editedAt DESC")
    List<Message> findEditedMessagesInChat(@Param("chat") Chat chat);

    // search messages by content
    @Query("SELECT m FROM Message m WHERE m.chat = :chat " +
            "AND LOWER(m.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "ORDER BY m.timestamp DESC")
    List<Message> searchMessagesByContent(@Param("chat") Chat chat,
                                          @Param("searchTerm") String searchTerm);

    // count total messages in a chat
    Long countByChat(Chat chat);

    // count messages sent by specific user in chat
    Long countByChatAndSender(Chat chat, User sender);

    // delete all messages in chat(clear history)
    void deleteByChat(Chat chat);


}
