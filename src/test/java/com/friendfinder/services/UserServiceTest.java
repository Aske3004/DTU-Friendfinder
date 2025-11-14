package com.friendfinder.services;



import com.friendfinder.repository.UserRepository;
import com.friendfinder.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    void testUpdateUserName() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Old Name");
        userRepository.save(user);

        // Act
        User updatedUser = userService.updateUserName("New Name", "test@example.com");

        // Assert
        assertThat(updatedUser.getName()).isEqualTo("New Name");
    }


    @Test
    @Transactional
    void testDeleteUser() {
        // Arrange: Opret en bruger
        User user = new User();
        user.setEmail("delete@example.com");
        user.setName("To Delete");
        userRepository.save(user);

        // Bekræft at brugeren findes
        assertThat(userRepository.findByEmail("delete@example.com")).isNotNull();

        // Act: Slet brugeren
        userService.deleteUser("delete@example.com");

        // Assert: Brugeren skal være fjernet
        assertThat(userRepository.findByEmail("delete@example.com")).isNull();
    }

}
