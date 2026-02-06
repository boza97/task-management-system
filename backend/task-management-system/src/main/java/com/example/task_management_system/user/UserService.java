package com.example.task_management_system.user;

import com.example.task_management_system.common.exception.EmailAlreadyExistsException;
import com.example.task_management_system.user.dto.UserRegistrationRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException();
        }

        String hashedPassword = passwordEncoder.encode(request.password());
        User user = new User(request.firstName(), request.lastName(), request.email(), hashedPassword);

        userRepository.save(user);
    }

    public void assignRole(UUID userId, String roleName) {
        User user = findUser(userId);
        Role role = findRole(roleName);

        user.getRoles().add(role);
    }

    public void removeRole(UUID userId, String roleName) {
        User user = findUser(userId);
        Role role = findRole(roleName);

        user.getRoles().remove(role);
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
                             .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private Role findRole(String roleName) {
        return roleRepository.findByName(roleName)
                             .orElseThrow(() -> new IllegalArgumentException("Role not found"));
    }
}
