package com.example.task_management_system.auth;

import com.example.task_management_system.auth.dto.LoginRequest;
import com.example.task_management_system.common.exception.InvalidCredentialsException;
import com.example.task_management_system.user.User;
import com.example.task_management_system.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Test", "User", "test@example.com", "encoded-password");
        user.setId(UUID.randomUUID());
    }

    @Test
    void shouldLoginWithValidCredentials() {
        LoginRequest request = new LoginRequest(user.getEmail(), "password");
        when(userRepository.findByEmailWithRoles(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        String token = authService.login(request);

        assertThat(token).isEqualTo("jwt-token");
    }

    @Test
    void shouldRejectUnknownEmail() {
        LoginRequest request = new LoginRequest("unknown@example.com", "password");
        when(userRepository.findByEmailWithRoles(request.email())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(jwtService, never()).generateToken(user);
    }

    @Test
    void shouldRejectWrongPassword() {
        LoginRequest request = new LoginRequest(user.getEmail(), "wrong-password");
        when(userRepository.findByEmailWithRoles(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(jwtService, never()).generateToken(user);
    }
}
