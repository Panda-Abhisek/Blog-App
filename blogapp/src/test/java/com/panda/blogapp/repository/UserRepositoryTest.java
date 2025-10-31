package com.panda.blogapp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.panda.blogapp.entity.User;

@DataJpaTest
@ActiveProfiles("test") // ensures test profile properties are applied
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user1;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("password1");
        user1 = userRepository.save(user1); // Save user and ensure managed state
    }

    @Test
    void testFindByUsernameReturnsUser() {
        Optional<User> optionalUser = userRepository.findByUsername("user1");
        assertThat(optionalUser).isPresent();
        assertThat(optionalUser.get().getUsername()).isEqualTo("user1");
    }

    @Test
    void testFindByUsernameReturnsEmptyForNonExistentUser() {
        Optional<User> optionalUser = userRepository.findByUsername("nonexistent");
        assertThat(optionalUser).isNotPresent();
    }
}
