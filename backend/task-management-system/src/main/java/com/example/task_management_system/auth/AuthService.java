package com.example.task_management_system.auth;

import com.example.task_management_system.auth.dto.LoginRequest;
import com.example.task_management_system.common.exception.InvalidCredentialsException;
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
                                  .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return jwtService.generateToken(user);
    }
}
