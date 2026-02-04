package com.example.task_management_system.auth;

import com.example.task_management_system.auth.dto.LoginRequest;
import com.example.task_management_system.user.User;
import com.example.task_management_system.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String login(LoginRequest request) {
        User user = userRepository.findByEmailWithRoles(request.email())
                                  .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return jwtService.generateToken(user);
    }
}
