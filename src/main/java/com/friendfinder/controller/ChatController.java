package com.friendfinder.controller;

import com.friendfinder.dto.ChatDTO;
import com.friendfinder.dto.MessageDTO;
import com.friendfinder.services.ChatService;
import com.friendfinder.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/api/chats")
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

    // ---------------- REST-DEL ----------------

    // Hent alle chats for en bruger
    @GetMapping
    public List<ChatDTO> getUserChats(@RequestParam("userId") Long userId) {
        return chatService.getUserChats(userId);
    }

    // Hent alle beskeder i en given chat
    @GetMapping("/{chatId}/messages")
    public List<MessageDTO> getChatMessages(@PathVariable Long chatId,
                                            @RequestParam("userId") Long requestingUserId) {
        return messageService.getChatMessages(chatId, requestingUserId);
    }


    @PostMapping("/{chatId}/messages")
    public MessageDTO sendMessageHttp(@PathVariable Long chatId,
                                      @RequestParam("userId") Long senderId,
                                      @RequestBody MessageDTO messageDTO) {

        // Sørg for at DTO matcher path/param
        messageDTO.setChatId(chatId);
        messageDTO.setSenderId(senderId);

        // Gem i DB + kør strategier
        MessageDTO saved = messageService.saveMessage(messageDTO, senderId);

        // Broadcast til alle som lytter på denne chat via WebSocket
        String destination = "/topic/chat/" + saved.getChatId();
        messagingTemplate.convertAndSend(destination, saved);

        return saved;
    }

    // ---------------- WEBSOCKET-DEL ----------------
    // Klient sender til: /app/chat.send
    @MessageMapping("/chat.send")
    public void sendMessageWebSocket(MessageDTO messageDTO) {
        Long senderId = messageDTO.getSenderId();
        if (senderId == null) {
            throw new IllegalArgumentException("senderId must be provided in MessageDTO when using WebSocket");
        }

        MessageDTO saved = messageService.saveMessage(messageDTO, senderId);

        // Send til alle subscribers på denne chat
        String destination = "/topic/chat/" + saved.getChatId();
        messagingTemplate.convertAndSend(destination, saved);
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
        messageService.markMessageAsread(request.getMessageId(), request.getUserId());

    }
}
