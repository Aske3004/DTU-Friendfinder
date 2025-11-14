package com.friendfinder.factory;

import com.friendfinder.model.Chat;
import com.friendfinder.model.User;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ChatFactory {

    // dm factory method
    public Chat createDirectChat(User user1, User user2) {
        String chatName = generateDirectChatName(user1, user2);
        Chat chat = new Chat(chatName, "DIRECT");
        chat.addParticipant(user1);
        chat.addParticipant(user2);
        return chat;
    }

    // gc factory method
    public Chat createGroupChat(String chatName, User creator, Set<User> participants) {
        Chat chat = new Chat(chatName, "GROUP");
        chat.addParticipant(creator);
        for (User participant : participants) {
            chat.addParticipant(participant);
        }
        return chat;
    }

    // system/announcement factory method
    public Chat createSystemChat(String chatName) {
        return new Chat(chatName, "SYSTEM");
    }

    // gc without starting participants
    public Chat createEmptyGroupChat(String chatName, User creator) {
        Chat chat = new Chat(chatName, "GROUP");
        chat.addParticipant(creator);
        return chat;
    }

    // generate consistent name for dm's
    private String generateDirectChatName(User user1, User user2) {
        String name1 = user1.getName();
        String name2 = user2.getName();

        if(name1.compareTo(name2) < 0) {
            return name1 + " & " + name2;
        } else {
            return name2 + " & " + name1;
        }
    }



}
