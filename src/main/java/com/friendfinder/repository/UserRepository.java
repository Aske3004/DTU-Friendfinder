package com.friendfinder.repository;

import com.friendfinder.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "user", path = "user")
public interface UserRepository extends PagingAndSortingRepository<User, Long>, CrudRepository<User, Long> {
    User findByEmail(@Param("email") String email);


    // find all participants in a specific chat
    @Query("SELECT u FROM User u JOIN u.chats c WHERE c.chatId = :chatId")
    List<User> findAllParticipantsInChat(@Param("chatId") Long chatId);

    // find users not in a specific chat (used when adding new people)
    @Query("SELECT u FROM User u WHERE u.userId NOT IN " +
           "(SELECT p.userId FROM Chat c JOIN c.participants p WHERE c.chatId = :chatId)")
    List<User> findUsersNotInChat(@Param("chatId") Long chatId);

    // count number of chats a user is in
    @Query("SELECT COUNT(c) FROM User u JOIN u.chats c WHERE u.userId = :userId")
    Long countChatsForUser(@Param("userId") Long userId);

    // find users who are in multiple chats with the specified user
    @Query("SELECT DISTINCT u FROM User u JOIN u.chats c1 JOIN c1.participants p WHERE p.userId = :userId AND u.userId != :userId")
    List<User> findUsersInCommonChatsWith(@Param("userId") Long userId);


}
