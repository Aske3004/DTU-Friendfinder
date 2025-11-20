package com.friendfinder.controller;


import static org.hamcrest.Matchers.instanceOf;
import com.friendfinder.model.User;
import com.friendfinder.services.AuthenticatorService;
import com.friendfinder.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticatorService authenticatorService;

    // ✅ GET /users/create-user
    @Test
    void testCreateUserPage() throws Exception {
        mockMvc.perform(get("/users/create-user"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-user"))
                .andExpect(model().attributeExists("name", "email", "password"));
    }

    // ✅ POST /users/create-user
    @Test
    void testPostCreateUserSuccess() throws Exception {
        User user = new User();
        user.setEmail("john@example.com");
        user.setName("John");
        user.setPassword("secret");

        // register() er void
        doNothing().when(authenticatorService).register(any(User.class));

        // authenticate() returnerer Auth med kun User
        when(authenticatorService.authenticate(eq("john@example.com"), eq("secret")))
                .thenReturn(new AuthenticatorService.Auth(user));

        mockMvc.perform(post("/users/create-user")
                        .param("name", "John")
                        .param("email", "john@example.com")
                        .param("password", "secret"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    // ✅ GET /users/all med auth
    @Test
    void testListUsersWithAuth() throws Exception {
        when(userService.findAllUsers()).thenReturn(java.util.List.of(new User()));

        mockMvc.perform(get("/users/all").sessionAttr("auth", new Object()))
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attributeExists("users"));
    }

    // ✅ GET /users/all uden auth
    @Test
    void testListUsersWithoutAuthRedirect() throws Exception {
        mockMvc.perform(get("/users/all"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }





    // ✅ POST /users/update-name
    @Test
    void testUpdateName() throws Exception {
        User user = new User();
        user.setEmail("john@example.com");

        mockMvc.perform(post("/users/update-name")
                        .sessionAttr("user", user)
                        .param("name", "NewName"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/user-profile"));

        verify(userService).updateUserName(eq("NewName"), eq("john@example.com"));
    }

    // ✅ POST /users/delete-user
    @Test
    void testDeleteUser() throws Exception {
        User user = new User();
        user.setEmail("john@example.com");

        mockMvc.perform(post("/users/delete-user")
                        .sessionAttr("user", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService).deleteUser(eq("john@example.com"));
    }
}