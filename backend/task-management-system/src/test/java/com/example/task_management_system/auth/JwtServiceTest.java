package com.example.task_management_system.auth;

import com.example.task_management_system.user.Role;
import com.example.task_management_system.user.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET =
            "test-secret-key-that-is-long-enough-for-hmac-sha-signing";

    @Test
    void shouldGenerateAndValidateToken() {
        JwtService jwtService = new JwtService(SECRET, 30);
        User user = new User("Test", "User", "test@example.com", "password");
        user.setId(UUID.randomUUID());
        user.getRoles().add(new Role("USER"));

        String token = jwtService.generateToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtService.validateAndGetUserId(token)).isEqualTo(user.getId());
    }
}
