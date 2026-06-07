package com.example.task_management_system.user;

import com.example.task_management_system.common.exception.EmailAlreadyExistsException;
import com.example.task_management_system.user.dto.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterUser() {
        UserRegistrationRequest request =
                new UserRegistrationRequest("test@example.com", "password", "Test", "User");
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");

        userService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo(request.email());
        assertThat(captor.getValue().getPassword()).isEqualTo("encoded-password");
    }

    @Test
    void shouldRejectExistingEmail() {
        UserRegistrationRequest request =
                new UserRegistrationRequest("test@example.com", "password", "Test", "User");
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any(User.class));
    }

    @Test
    void shouldReturnAllUsers() {
        User first = createUser("first@example.com");
        User second = createUser("second@example.com");
        when(userRepository.findAll()).thenReturn(List.of(first, second));

        var result = userService.getAllUsers();

        assertThat(result)
                .extracting(user -> user.email())
                .containsExactly("first@example.com", "second@example.com");
    }

    @Test
    void shouldAssignRole() {
        User user = createUser("test@example.com");
        Role role = new Role("ADMIN");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role));

        userService.assignRole(user.getId(), "ADMIN");

        assertThat(user.getRoles()).contains(role);
    }

    @Test
    void shouldThrowWhenAssignedRoleDoesNotExist() {
        User user = createUser("test@example.com");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.assignRole(user.getId(), "ADMIN"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Role not found");
    }

    private User createUser(String email) {
        User user = new User("Test", "User", email, "password");
        user.setId(UUID.randomUUID());
        return user;
    }
}
