package com.friendfinder.controller;

import com.friendfinder.dto.ChatDTO;
import com.friendfinder.dto.MessageDTO;
import com.friendfinder.dto.request.*;
import com.friendfinder.exceptions.*;
import com.friendfinder.services.ChatService;
import com.friendfinder.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/chats")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ChatController(ChatService chatService,
                          MessageService messageService,
                          SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }

    // ---------------- REST-Endpoints ----------------

    // Hent alle chats for en bruger
    @GetMapping
    public ResponseEntity<List<ChatDTO>> getUserChats(@RequestParam("userId") Long userId) {
        try {
            List<ChatDTO> chats = chatService.getUserChats(userId);
            return ResponseEntity.ok(chats);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<ChatDTO> getChatById(@PathVariable Long chatId){
        try {
            ChatDTO chat = chatService.getChatById(chatId);
            return ResponseEntity.ok(chat);
        } catch (ChatNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/direct")
    public ResponseEntity<ChatDTO> createDirectChat(@RequestParam("user1Id") Long user1Id,
                                                    @RequestParam("user2Id") Long user2Id) {
        try {
            ChatDTO chatDTO = chatService.createDirectChat(user1Id, user2Id);
            return ResponseEntity.status(HttpStatus.CREATED).body(chatDTO);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/group")
    public ResponseEntity<ChatDTO> createGroupChat(@Valid @RequestBody CreateGroupChatRequest request) {
        try {
            ChatDTO chat = chatService.createGroupChat(
                    request.getChatName(),
                    request.getCreatorID(),
                    request.getParticipantIDs()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(chat);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{chatId}/participants")
    public ResponseEntity<Void> addParticipant(@PathVariable Long chatId,
                                               @RequestParam("userId") Long userId) {
        try {
            chatService.addParticipantToChat(chatId, userId);
            return ResponseEntity.ok().build();
        } catch (ChatNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (DuplicateParticipantException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{chatId}/participants")
    public ResponseEntity<Void> removeParticipant(@PathVariable Long chatId,
                                                  @RequestParam("userId") Long userId) {
        try {
            chatService.removeParticipantFromChat(chatId, userId);
            return ResponseEntity.ok().build();
        } catch (ChatNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UserNotInChatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }



    // Hent alle beskeder i en given chat
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<MessageDTO>> getChatMessages(@PathVariable Long chatId,
                                            @RequestParam("userId") Long requestingUserId) {
        try{
            List<MessageDTO> messages = messageService.getChatMessages(chatId, requestingUserId);
            return ResponseEntity.ok(messages);
        } catch (ChatNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedMessageAccessException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }


    @PostMapping("/{chatId}/messages")
    public ResponseEntity<MessageDTO> sendMessageHttp(@PathVariable Long chatId,
                                      @RequestParam("userId") Long authenticatedUserId,
                                      @Valid @RequestBody SendMessageRequest request) {
        try {
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setChatId(chatId);
            messageDTO.setContent(request.getContent());
            messageDTO.setType(request.getType());

            MessageDTO saved = messageService.saveMessage(messageDTO, authenticatedUserId);

            String destination = "/topic/chat/" + saved.getChatId();
            messagingTemplate.convertAndSend(destination, saved);

            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (ChatNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UserNotInChatException | UnauthorizedMessageAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (InvalidMessageContentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/messages/{messageId}/read")
    public ResponseEntity<Void> markAsReadHttp(@PathVariable Long messageId,
                                               @RequestParam("userId") Long userId) {
        try {
            messageService.markMessageAsRead(messageId, userId);
            return ResponseEntity.ok().build();
        } catch (MessageNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedMessageAccessException | UserNotInChatException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (DuplicateReadReceiptException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/messages/{messageId}")
    public ResponseEntity<MessageDTO> editMessage(@PathVariable Long messageId,
                                                  @RequestParam("userId") Long userId,
                                                  @Valid @RequestBody EditMessageRequest request) {
        try {
            MessageDTO edited = messageService.editMessage(messageId, userId, request.getContent());
            messagingTemplate.convertAndSend("/topic/chat/" + edited.getChatId(), edited);
            return ResponseEntity.ok(edited);
        } catch (MessageNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedMessageAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (InvalidMessageContentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId,
                                              @RequestParam("userId") Long userId) {
        try {
            messageService.deleteMessage(messageId, userId);
            return ResponseEntity.noContent().build();
        } catch (MessageNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedMessageAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }


    // ---------------- WEBSOCKET-DEL ----------------
    // Klient sender til: /app/chat.send
    @MessageMapping("/chat.send")
    public void sendMessageWebSocket(@Payload MessageDTO messageDTO) {
        Long senderId = messageDTO.getSenderId();
        if (senderId == null) {
            throw new IllegalArgumentException("senderId must be provided in MessageDTO when using WebSocket");
        }

        try {
            MessageDTO saved = messageService.saveMessage(messageDTO, senderId);
            String destination = "/topic/chat/" + saved.getChatId();
            messagingTemplate.convertAndSend(destination, saved);
        } catch (ChatNotFoundException | UserNotFoundException e) {
            sendErrorToUser(senderId, "Chat or user not found");
        } catch (UserNotInChatException | UnauthorizedMessageAccessException e) {
            sendErrorToUser(senderId, "User not authorized to send message in this chat");
        } catch (InvalidMessageContentException e) {
            sendErrorToUser(senderId, "Invalid message content");
        }
    }

    @MessageMapping("/chat.read")
    public void markMessageAsReadWebSocket(@Payload ReadReceiptRequest request) {
        if (request.getMessageID() == null || request.getUserID() == null) {
            throw new IllegalArgumentException("messageId and userId required");
        }

        try {
            messageService.markMessageAsRead(request.getMessageID(), request.getUserID());
        } catch (DuplicateReadReceiptException e) {
            // Silently ignore duplicate read receipts
        } catch (Exception e) {
            sendErrorToUser(request.getUserID(), "Failed to mark as read");
        }
    }

    @MessageMapping("/chat.join")
    public void userJoinChat(@Payload JoinChatRequest request) {
        if (request.getChatID() == null || request.getUserID() == null) {
            throw new IllegalArgumentException("chatID and userID must be provided");
        }
        try {
            MessageDTO joinMessage = new MessageDTO();
            joinMessage.setChatId(request.getChatID());
            joinMessage.setSenderId(request.getUserID());
            joinMessage.setSenderName(request.getUsername());
            joinMessage.setType(MessageDTO.MessageType.JOIN);
            joinMessage.setContent(request.getUsername() + " joined the chat");

            MessageDTO saved = messageService.saveMessage(joinMessage, request.getUserID());
            messagingTemplate.convertAndSend("/topic/chat/" + saved.getChatId(), saved);
        } catch (Exception e) {
            sendErrorToUser(request.getUserID(), "Failed to send join chat");
        }
    }

    @MessageMapping("/chat.leave")
    public void userLeaveChat(@Payload LeaveChatRequest request) {
        try{
            MessageDTO leaveMessage = new MessageDTO();
            leaveMessage.setChatId(request.getChatID());
            leaveMessage.setSenderId(request.getUserID());
            leaveMessage.setSenderName(request.getUsername());
            leaveMessage.setType(MessageDTO.MessageType.LEAVE);
            leaveMessage.setContent(request.getUsername() + " left the chat");

            MessageDTO saved = messageService.saveMessage(leaveMessage, request.getUserID());
            messagingTemplate.convertAndSend("/topic/chat/" + saved.getChatId(), saved);
        } catch (Exception e) {
            //Silently fail
        }
    }

    private void sendErrorToUser(Long userId, String errorMessage) {
        ErrorResponse error = new ErrorResponse(errorMessage);
        messagingTemplate.convertAndSend("/user/" + userId + "/queue/errors", error);
    }

    // Simple inner class for errors - doesnt need own class
    public static class ErrorResponse {
        private String message;
        private long timestamp;

        public ErrorResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        public String getMessage() {return message; }
        public void setMessage(String message) { this.message = message; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    //  "bruger har læst besked"

    public static class ReadRequest {
        private Long messageId;
        private Long userId;

        public Long getMessageId() {
            return messageId;
        }
        public void setMessageId(Long messageId) {
            this.messageId = messageId;
        }
        public Long getUserId() {
            return userId;
        }
        public void setUserId(Long userId) {
            this.userId = userId;
        }
    }

    @MessageMapping("/chat.read")
    public void markMessageAsRead(ReadRequest request) {
        if (request.getMessageId() == null || request.getUserId() == null) {
            throw new IllegalArgumentException("messageId and userId must be provided");
        }
        messageService.markMessageAsRead(request.getMessageId(), request.getUserId());

    }
}
