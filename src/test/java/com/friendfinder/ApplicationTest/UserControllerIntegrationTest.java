
package com.friendfinder.ApplicationTest;

import com.friendfinder.model.Interest;
import com.friendfinder.model.User;
import com.friendfinder.repository.InterestRepository;
import com.friendfinder.repository.UserRepository;
import com.friendfinder.services.AuthenticatorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerIntegrationTest {

    @Autowired private MockMvc mvc;
    @Autowired private UserRepository userRepository;
    @Autowired private InterestRepository interestRepository;
    @Autowired private AuthenticatorService authenticatorService; // bruges indirekte via controller

    // -------------------------------------------------------
    // 1) GET /users/create-user (viser form + seedede interesser)
    // -------------------------------------------------------
    @Test
    void testGetCreateUserPage() throws Exception {
        // InterestSeeder har allerede oprettet: Sports, Music, Gaming, Boardgames, Coding, Fitness, Reading
        // Vi validerer blot at siden loader og at "interests" er i modellen
        mvc.perform(get("/users/create-user"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-user"))
                .andExpect(model().attributeExists("name", "email", "password", "interests"));
    }

    // -------------------------------------------------------
    // 2) POST /users/create-user (opretter bruger med seedede interesser)
    // -------------------------------------------------------

    @Test
    @Transactional
    void testPostCreateUserWithInterests() throws Exception {
        Interest music  = interestRepository.findByName("Music");
        Interest sports = interestRepository.findByName("Sports");
        assertNotNull(music);
        assertNotNull(sports);

        mvc.perform(post("/users/create-user")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "John")
                        .param("email", "s123456@student.dtu.dk")
                        .param("password", "password123")
                        .param("selectedInterests", String.valueOf(music.getId()))
                        .param("selectedInterests", String.valueOf(sports.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        User saved = userRepository.findByEmail("s123456@student.dtu.dk");
        assertNotNull(saved);
        assertEquals("John", saved.getName());
        assertEquals(2, saved.getInterests().size()); // virker nu
    }


    // -------------------------------------------------------
    // 3) GET /users/all (redirect hvis ikke logget ind)
    // -------------------------------------------------------
    @Test
    void testListUsersRedirectIfNotLoggedIn() throws Exception {
        mvc.perform(get("/users/all"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    // -------------------------------------------------------
    // 3b) GET /users/all (viser liste når logget ind)
    // -------------------------------------------------------
    @Test
    void testListUsersAfterLogin() throws Exception {
        User u = new User();
        u.setName("Alice");
        u.setEmail("s654321@student.dtu.dk");
        u.setPassword("password123");
        userRepository.save(u);

        // Simuler “login” ved at lægge Auth i session (som din controller forventer)
        var auth = new AuthenticatorService.Auth(u);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("auth", auth);

        mvc.perform(get("/users/all").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attributeExists("users", "currentUser", "sentRequestEmails", "friendEmails"));
    }

    // -------------------------------------------------------
    // 4) GET /users/user-profile (viser profil + interesser)
    // -------------------------------------------------------

    @Test
    void testUserProfilePage() throws Exception {
        Interest coding = interestRepository.findByName("Coding");
        assertNotNull(coding);

        User u = new User();
        u.setName("Alice");
        u.setEmail("s654321@student.dtu.dk");
        u.setPassword("password123");
        u.setInterests(List.of(coding));
        userRepository.save(u);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", u);
        session.setAttribute("name", u.getName());
        session.setAttribute("email", u.getEmail());
        session.setAttribute("auth", new AuthenticatorService.Auth(u)); // ✅ tilføjet

        mvc.perform(get("/users/user-profile").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("user-profile"))
                .andExpect(model().attributeExists("name", "email", "password", "interests"));
    }


    // -------------------------------------------------------
    // 5) POST /users/update-name (success)
    // -------------------------------------------------------
    @Test
    void testUpdateNameSuccess() throws Exception {
        User u = new User();
        u.setName("Alice");
        u.setEmail("s654321@student.dtu.dk");
        u.setPassword("password123");
        userRepository.save(u);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", u);

        mvc.perform(post("/users/update-name")
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "NewName"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/user-profile"));

        User updated = userRepository.findByEmail("s654321@student.dtu.dk");
        assertEquals("NewName", updated.getName());
    }


    // -------------------------------------------------------
    // 7) POST /users/delete-user (success)
    // -------------------------------------------------------
    @Test
    void testDeleteUserSuccess() throws Exception {
        User u = new User();
        u.setName("Alice");
        u.setEmail("s654321@student.dtu.dk");
        u.setPassword("password123");
        userRepository.save(u);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", u);

        mvc.perform(post("/users/delete-user").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        assertNull(userRepository.findByEmail("s654321@student.dtu.dk"));
    }

    // -------------------------------------------------------
    // 8) Negative: invalid email -> bliver på create-user
    // -------------------------------------------------------
    @Test
    void testPostCreateUserInvalidEmail() throws Exception {
        Interest fitness = interestRepository.findByName("Fitness");
        assertNotNull(fitness);

        mvc.perform(post("/users/create-user")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "John")
                        .param("email", "bad-email") // ugyldig ift. regex i User.isEmailValid
                        .param("password", "password123")
                        .param("selectedInterests", String.valueOf(fitness.getId())))
                .andExpect(status().isOk())                 // ikke redirect
                .andExpect(view().name("create-user"))      // samme view
                .andExpect(model().attributeExists("user", "name", "email", "password", "interests"));
    }

    // -------------------------------------------------------
    // 9) Negative: invalid password -> bliver på create-user
    // -------------------------------------------------------
    @Test
    void testPostCreateUserInvalidPassword() throws Exception {
        Interest reading = interestRepository.findByName("Reading");
        assertNotNull(reading);

        mvc.perform(post("/users/create-user")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "John")
                        .param("email", "s123456@student.dtu.dk")
                        .param("password", "short") // < 8 tegn — forventer InvalidPasswordException i register(...)
                        .param("selectedInterests", String.valueOf(reading.getId())))
                .andExpect(status().isOk())
                .andExpect(view().name("create-user"))
                .andExpect(model().attributeExists("user", "name", "email", "password", "interests"));
    }
}
