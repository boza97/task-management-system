package com.example.task_management_system.common.security;

import com.example.task_management_system.user.User;
import com.example.task_management_system.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        return userRepository.findByEmailWithRoles(email)
                             .orElseThrow(() -> new IllegalStateException("User not found"));
    }
}
