package com.friendfinder.repository;

import com.friendfinder.model.Message;
import com.friendfinder.model.MessageReadReceipt;
import com.friendfinder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageReadReceiptRepository extends JpaRepository<MessageReadReceipt, Long> {

    // find all read receipts for a message
    List<MessageReadReceipt> findByMessage(Message message);

    // find read receipt for a specific user and message
    Optional<MessageReadReceipt> findByMessageAndUser(Message message, User user);

    // check if user has read a specific message
    boolean existsByMessageAndUser(Message message, User user);

    // get all messages a user has read
    List<MessageReadReceipt> findByUser(User user);

    // count how many users read a message
    @Query("SELECT COUNT(r) FROM MessageReadReceipt r WHERE r.message = :message")
    int countByMessage(@Param("message") Message message);

    // count how many message a user has read in a specific chat
    @Query("SELECT COUNT(r) FROM MessageReadReceipt r WHERE r.user = :user AND r.message.chat.chatId = :chatId")
    int countReadMessagesInChatByUser(@Param("user") User user, @Param("chatId") Long chatId);

    // find users who have read a specific message
    @Query("SELECT r.user FROM MessageReadReceipt r WHERE r.message = :message")
    List<User> findUsersWhoReadMessage(@Param("message") Message message);

    // find all read receipts in a chat
    @Query("SELECT r FROM MessageReadReceipt r WHERE r.message.chat.chatId = :chatId")
    List<MessageReadReceipt> findAllInChat(@Param("chatId") Long chatId);

    // delete all read receipts for a message(usefull when deleting message)
    void deleteByMessage(Message message);

    // delete all read receipts for a user (usefull when deleting user)
    void deleteByUser(User user);


}
