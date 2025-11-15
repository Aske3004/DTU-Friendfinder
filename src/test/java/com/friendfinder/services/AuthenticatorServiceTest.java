package com.friendfinder.services;

import com.friendfinder.exceptions.InvalidEmailException;
import com.friendfinder.exceptions.InvalidPasswordException;
import com.friendfinder.model.User;
import com.friendfinder.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthenticatorServiceTest {

    private AuthenticatorService service;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() throws Exception {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);

        service = new AuthenticatorService();

        var repoField = AuthenticatorService.class.getDeclaredField("userRepository");
        repoField.setAccessible(true);
        repoField.set(service, userRepository);

        var encField = AuthenticatorService.class.getDeclaredField("passwordEncoder");
        encField.setAccessible(true);
        encField.set(service, passwordEncoder);
    }

    @Test
    void authenticate_nullEmail_shouldThrowInvalidEmailException() {
        assertThrows(InvalidEmailException.class,
                () -> service.authenticate(null, "secret123"));
    }

    @Test
    void authenticate_emptyEmail_shouldThrowInvalidEmailException() {
        assertThrows(InvalidEmailException.class,
                () -> service.authenticate("", "secret123"));
    }

    @Test
    void authenticate_nullPassword_shouldThrowInvalidPasswordException() {
        assertThrows(InvalidPasswordException.class,
                () -> service.authenticate("s123456@student.dtu.dk", null));
    }

    @Test
    void authenticate_emptyPassword_shouldThrowInvalidPasswordException() {
        assertThrows(InvalidPasswordException.class,
                () -> service.authenticate("s123456@student.dtu.dk", ""));
    }

    @Test
    void authenticate_unknownEmail_shouldThrowInvalidEmailException() {

        when(userRepository.findByEmail("s123456@student.dtu.dk")).thenReturn(null);

        assertThrows(InvalidEmailException.class,
                () -> service.authenticate("s123456@student.dtu.dk", "whatever"));
    }

    @Test
    void authenticate_validEmail_isLowercasedBeforeLookup() throws Exception {
        var dbUser = new User();
        dbUser.setEmail("s123456@student.dtu.dk");
        dbUser.setPassword("hashed");

        when(userRepository.findByEmail("s123456@student.dtu.dk")).thenReturn(dbUser);
        when(passwordEncoder.matches("secret123", "hashed")).thenReturn(true);

        var auth = service.authenticate("S123456@student.dtu.dk", "secret123");

        assertNotNull(auth);
        assertEquals("s123456@student.dtu.dk", auth.user().getEmail());
        verify(userRepository).findByEmail("s123456@student.dtu.dk");
    }

    @Test
    void authenticate_wrongPassword_shouldThrowInvalidPasswordException() {
        var dbUser = new User();
        dbUser.setEmail("s123456@student.dtu.dk");
        dbUser.setPassword("encoded");

        when(userRepository.findByEmail("s123456@student.dtu.dk")).thenReturn(dbUser);
        when(passwordEncoder.matches("badpass", "encoded")).thenReturn(false);

        assertThrows(InvalidPasswordException.class,
                () -> service.authenticate("s123456@student.dtu.dk", "badpass"));
    }

    @Test
    void authenticate_validCredentials_shouldReturnAuth() throws Exception {
        var dbUser = new User();
        dbUser.setEmail("s654321@student.dtu.dk");
        dbUser.setPassword("encoded");

        when(userRepository.findByEmail("s654321@student.dtu.dk")).thenReturn(dbUser);
        when(passwordEncoder.matches("GoodPass123", "encoded")).thenReturn(true);

        var auth = service.authenticate("s654321@student.dtu.dk", "GoodPass123");

        assertNotNull(auth);
        assertEquals("s654321@student.dtu.dk", auth.user().getEmail());
    }

    @Test
    void register_invalidEmailFormat_shouldThrowInvalidEmailException() {
        var u = new User();
        u.setName("Alice");
        u.setEmail("alice@gmail.com");
        u.setPassword("Strong123");

        assertThrows(InvalidEmailException.class, () -> service.register(u));

        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_validEmail_shouldEncodeAndSave() throws Exception {
        var u = new User();
        u.setName("Bob");
        u.setEmail("s111111@student.dtu.dk");
        u.setPassword("Secret1234");

        when(userRepository.findByEmail("s111111@student.dtu.dk")).thenReturn(null);
        when(passwordEncoder.encode("Secret1234")).thenReturn("ENC");

        service.register(u);

        assertEquals("ENC", u.getPassword());
        verify(userRepository).save(u);
    }
}
