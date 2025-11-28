
package com.friendfinder.controller;

import com.friendfinder.model.Interest;
import com.friendfinder.model.User;
import com.friendfinder.services.AuthenticatorService;
import com.friendfinder.services.FriendService;
import com.friendfinder.services.UserService;
import com.friendfinder.repository.InterestRepository;
import com.friendfinder.exceptions.InvalidEmailException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private UserService userService;
    @MockBean private AuthenticatorService authenticatorService;
    @MockBean private FriendService friendService;
    @MockBean private InterestRepository interestRepository;

    // -------------------------------------------------------
    // GET /users/create-user
    // -------------------------------------------------------
    @Test
    void getCreateUser_returns200() throws Exception {
        when(interestRepository.findAll()).thenReturn(List.of(new Interest()));

        mockMvc.perform(get("/users/create-user"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-user"))
                .andExpect(model().attributeExists("name", "email", "password", "interests"));
    }

    // -------------------------------------------------------
    // POST /users/create-user (success)
    // -------------------------------------------------------
    @Test
    void postCreateUser_success_redirectsToHome() throws Exception {
        doNothing().when(authenticatorService).register(any(User.class));
        when(authenticatorService.authenticate(anyString(), anyString()))
                .thenReturn(new AuthenticatorService.Auth(new User()));

        when(interestRepository.findAllById(anyList())).thenReturn(List.of(new Interest()));

        mockMvc.perform(post("/users/create-user")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "John")
                        .param("email", "s123456@student.dtu.dk")
                        .param("password", "password123")
                        .param("selectedInterests", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    // -------------------------------------------------------
    // POST /users/create-user (invalid email -> stays on page)
    // -------------------------------------------------------
    @Test
    void postCreateUser_invalidEmail_returnsCreateUserView() throws Exception {
        doThrow(new InvalidEmailException("Invalid email"))
                .when(authenticatorService).register(any(User.class));

        when(interestRepository.findAll()).thenReturn(List.of(new Interest()));

        mockMvc.perform(post("/users/create-user")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "John")
                        .param("email", "bad-email")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-user"))
                .andExpect(model().attributeExists("name", "email", "password", "interests"));
    }

    // -------------------------------------------------------
    // GET /users/all (redirect if not logged in)
    // -------------------------------------------------------
    @Test
    void getAllUsers_notLoggedIn_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/users/all"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    // -------------------------------------------------------
    // GET /users/all (logged in -> returns users view)
    // -------------------------------------------------------
    @Test
    void getAllUsers_loggedIn_returnsUsersView() throws Exception {
        User currentUser = new User();
        currentUser.setEmail("s123456@student.dtu.dk");

        when(userService.findAllUsers()).thenReturn(List.of(currentUser));
        when(friendService.getPendingRequestsAsSender(any(User.class))).thenReturn(List.of());
        when(friendService.getPendingRequestsAsReceiver(any(User.class))).thenReturn(List.of());
        when(friendService.getFriends(any(User.class))).thenReturn(List.of());

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("auth", new AuthenticatorService.Auth(currentUser));

        mockMvc.perform(get("/users/all").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attributeExists("users", "currentUser", "sentRequestEmails", "friendEmails"));
    }

    // -------------------------------------------------------
    // POST /users/update-name (success)
    // -------------------------------------------------------
    @Test
    void updateName_success_redirectsToProfile() throws Exception {
        when(userService.updateUserName(anyString(), anyString()))
                .thenReturn(new User()); // ✅ korrekt for non-void metode

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new User());

        mockMvc.perform(post("/users/update-name")
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "NewName"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/user-profile"));
    }


    // -------------------------------------------------------
    // POST /users/delete-user (success)
    // -------------------------------------------------------
    @Test
    void deleteUser_success_redirectsToLogin() throws Exception {
        doNothing().when(userService).deleteUser(anyString());

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new User());

        mockMvc.perform(post("/users/delete-user").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    // -------------------------------------------------------
    // POST /users/update-interest (success)
    // -------------------------------------------------------
    @Test
    void updateInterest_success_redirectsToProfile() throws Exception {
        doNothing().when(userService).updateUserInterest(anyList(), anyString());
        when(interestRepository.findAllById(anyList())).thenReturn(List.of(new Interest()));

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new User());

        mockMvc.perform(post("/users/update-interest")
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("selectedInterests", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/user-profile"));
    }
}
