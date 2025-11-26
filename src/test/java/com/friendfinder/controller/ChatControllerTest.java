package com.friendfinder.controller;

import com.friendfinder.dto.ChatDTO;
import com.friendfinder.dto.MessageDTO;
import com.friendfinder.dto.request.CreateGroupChatRequest;
import com.friendfinder.dto.request.EditMessageRequest;
import com.friendfinder.dto.request.SendMessageRequest;
import com.friendfinder.exceptions.*;
import com.friendfinder.services.ChatService;
import com.friendfinder.services.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @MockBean
    private MessageService messageService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Test
    void getUserChats_returns200() throws Exception {
        when(chatService.getUserChats(1L)).thenReturn(List.of(new ChatDTO()));

        mockMvc.perform(get("/api/chats")
                        .param("userId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getUserChats_userNotFound_returns404() throws Exception {
        when(chatService.getUserChats(1L))
                .thenThrow(new UserNotFoundException("not found"));

        mockMvc.perform(get("/api/chats")
                        .param("userId", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserChats_unexpectedError_returns500() throws Exception {
        when(chatService.getUserChats(1L))
                .thenThrow(new RuntimeException("boom"));

        mockMvc.perform(get("/api/chats")
                        .param("userId", "1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getChatById_returns200() throws Exception {
        when(chatService.getChatById(5L)).thenReturn(new ChatDTO());

        mockMvc.perform(get("/api/chats/5"))
                .andExpect(status().isOk());
    }

    @Test
    void getChatById_notFound_returns404() throws Exception {
        when(chatService.getChatById(5L))
                .thenThrow(new ChatNotFoundException("not found"));

        mockMvc.perform(get("/api/chats/5"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createDirectChat_returns201() throws Exception {
        when(chatService.createDirectChat(1L, 2L))
                .thenReturn(new ChatDTO());

        mockMvc.perform(post("/api/chats/direct")
                        .param("user1Id", "1")
                        .param("user2Id", "2"))
                .andExpect(status().isCreated());
    }

    @Test
    void createDirectChat_userNotFound_returns404() throws Exception {
        when(chatService.createDirectChat(1L, 2L))
                .thenThrow(new UserNotFoundException("nope"));

        mockMvc.perform(post("/api/chats/direct")
                        .param("user1Id", "1")
                        .param("user2Id", "2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createGroupChat_returns201() throws Exception {
        String json = """
        {
          "chatName": "Study Group",
          "creatorID": 1,
          "participantIDs": [2, 3]
        }
        """;

        when(chatService.createGroupChat(eq("Study Group"), eq(1L), eq(Set.of(2L, 3L))))
                .thenReturn(new ChatDTO());

        mockMvc.perform(post("/api/chats/group")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void createGroupChat_userNotFound_returns404() throws Exception {
        String json = """
        {
          "chatName": "Study Group",
          "creatorID": 1,
          "participantIDs": [2, 3]
        }
        """;

        when(chatService.createGroupChat(anyString(), anyLong(), anySet()))
                .thenThrow(new UserNotFoundException("nope"));

        mockMvc.perform(post("/api/chats/group")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void addParticipant_returns200() throws Exception {
        doNothing().when(chatService).addParticipantToChat(10L, 20L);

        mockMvc.perform(post("/api/chats/10/participants")
                        .param("userId", "20"))
                .andExpect(status().isOk());
    }

    @Test
    void addParticipant_notFound_returns404() throws Exception {
        doThrow(new ChatNotFoundException("no chat"))
                .when(chatService).addParticipantToChat(10L, 20L);

        mockMvc.perform(post("/api/chats/10/participants")
                        .param("userId", "20"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addParticipant_duplicate_returns409() throws Exception {
        doThrow(new DuplicateParticipantException("dup"))
                .when(chatService).addParticipantToChat(10L, 20L);

        mockMvc.perform(post("/api/chats/10/participants")
                        .param("userId", "20"))
                .andExpect(status().isConflict());
    }

    @Test
    void removeParticipant_returns200() throws Exception {
        doNothing().when(chatService).removeParticipantFromChat(10L, 20L);

        mockMvc.perform(delete("/api/chats/10/participants")
                        .param("userId", "20"))
                .andExpect(status().isOk());
    }

    @Test
    void removeParticipant_notFound_returns404() throws Exception {
        doThrow(new UserNotFoundException("no user"))
                .when(chatService).removeParticipantFromChat(10L, 20L);

        mockMvc.perform(delete("/api/chats/10/participants")
                        .param("userId", "20"))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeParticipant_notInChat_returns400() throws Exception {
        doThrow(new UserNotInChatException("nope"))
                .when(chatService).removeParticipantFromChat(10L, 20L);

        mockMvc.perform(delete("/api/chats/10/participants")
                        .param("userId", "20"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getChatMessages_returns200() throws Exception {
        when(messageService.getChatMessages(5L, 1L))
                .thenReturn(List.of(new MessageDTO()));

        mockMvc.perform(get("/api/chats/5/messages")
                        .param("userId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getChatMessages_notFound_returns404() throws Exception {
        when(messageService.getChatMessages(5L, 1L))
                .thenThrow(new ChatNotFoundException("no chat"));

        mockMvc.perform(get("/api/chats/5/messages")
                        .param("userId", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getChatMessages_forbidden_returns403() throws Exception {
        when(messageService.getChatMessages(5L, 1L))
                .thenThrow(new UnauthorizedMessageAccessException("forbidden"));

        mockMvc.perform(get("/api/chats/5/messages")
                        .param("userId", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void sendMessageHttp_returns201() throws Exception {
        String json = """
        {
          "content": "Hello",
          "type": "CHAT"
        }
        """;

        MessageDTO saved = new MessageDTO();
        saved.setChatId(5L);

        when(messageService.saveMessage(any(MessageDTO.class), eq(1L))).thenReturn(saved);

        mockMvc.perform(post("/api/chats/5/messages")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void sendMessageHttp_notFound_returns404() throws Exception {
        String json = """
        {
          "content": "Hello",
          "type": "CHAT"
        }
        """;

        when(messageService.saveMessage(any(MessageDTO.class), eq(1L)))
                .thenThrow(new ChatNotFoundException("no chat"));

        mockMvc.perform(post("/api/chats/5/messages")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void sendMessageHttp_forbidden_returns403() throws Exception {
        String json = """
        {
          "content": "Hello",
          "type": "CHAT"
        }
        """;

        when(messageService.saveMessage(any(MessageDTO.class), eq(1L)))
                .thenThrow(new UnauthorizedMessageAccessException("no"));

        mockMvc.perform(post("/api/chats/5/messages")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    void sendMessageHttp_invalidContent_returns400() throws Exception {
        String json = """
        {
          "content": "",
          "type": "CHAT"
        }
        """;

        when(messageService.saveMessage(any(MessageDTO.class), eq(1L)))
                .thenThrow(new InvalidMessageContentException("empty"));

        mockMvc.perform(post("/api/chats/5/messages")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void markAsReadHttp_returns200() throws Exception {
        doNothing().when(messageService).markMessageAsRead(100L, 1L);

        mockMvc.perform(post("/api/chats/messages/100/read")
                        .param("userId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void markAsReadHttp_notFound_returns404() throws Exception {
        doThrow(new MessageNotFoundException("no msg"))
                .when(messageService).markMessageAsRead(100L, 1L);

        mockMvc.perform(post("/api/chats/messages/100/read")
                        .param("userId", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void markAsReadHttp_forbidden_returns403() throws Exception {
        doThrow(new UnauthorizedMessageAccessException("forbidden"))
                .when(messageService).markMessageAsRead(100L, 1L);

        mockMvc.perform(post("/api/chats/messages/100/read")
                        .param("userId", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void markAsReadHttp_duplicate_returns409() throws Exception {
        doThrow(new DuplicateReadReceiptException("dup"))
                .when(messageService).markMessageAsRead(100L, 1L);

        mockMvc.perform(post("/api/chats/messages/100/read")
                        .param("userId", "1"))
                .andExpect(status().isConflict());
    }

    @Test
    void editMessage_returns200() throws Exception {
        String json = """
        {
          "content": "Edited text"
        }
        """;

        MessageDTO edited = new MessageDTO();
        edited.setChatId(5L);

        when(messageService.editMessage(100L, 1L, "Edited text"))
                .thenReturn(edited);

        mockMvc.perform(put("/api/chats/messages/100")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void editMessage_notFound_returns404() throws Exception {
        String json = """
        {
          "content": "Edited text"
        }
        """;

        when(messageService.editMessage(eq(100L), eq(1L), anyString()))
                .thenThrow(new MessageNotFoundException("no msg"));

        mockMvc.perform(put("/api/chats/messages/100")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void editMessage_forbidden_returns403() throws Exception {
        String json = """
        {
          "content": "Edited text"
        }
        """;

        when(messageService.editMessage(eq(100L), eq(1L), anyString()))
                .thenThrow(new UnauthorizedMessageAccessException("no"));

        mockMvc.perform(put("/api/chats/messages/100")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    void editMessage_invalidContent_returns400() throws Exception {
        String json = """
        {
          "content": ""
        }
        """;

        when(messageService.editMessage(eq(100L), eq(1L), anyString()))
                .thenThrow(new InvalidMessageContentException("empty"));

        mockMvc.perform(put("/api/chats/messages/100")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteMessage_returns204() throws Exception {
        doNothing().when(messageService).deleteMessage(100L, 1L);

        mockMvc.perform(delete("/api/chats/messages/100")
                        .param("userId", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteMessage_notFound_returns404() throws Exception {
        doThrow(new MessageNotFoundException("no msg"))
                .when(messageService).deleteMessage(100L, 1L);

        mockMvc.perform(delete("/api/chats/messages/100")
                        .param("userId", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMessage_forbidden_returns403() throws Exception {
        doThrow(new UnauthorizedMessageAccessException("no access"))
                .when(messageService).deleteMessage(100L, 1L);

        mockMvc.perform(delete("/api/chats/messages/100")
                        .param("userId", "1"))
                .andExpect(status().isForbidden());
    }
}

