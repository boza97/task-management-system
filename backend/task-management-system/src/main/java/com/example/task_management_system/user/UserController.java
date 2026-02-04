package com.example.task_management_system.user;

import com.example.task_management_system.user.dto.RoleAssignmentRequest;
import com.example.task_management_system.user.dto.UserRegistrationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody UserRegistrationRequest request) {
        userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{userId}/roles")
    public ResponseEntity<Void> assignRole(@PathVariable UUID userId,
                                           @Valid @RequestBody RoleAssignmentRequest request) {
        userService.assignRole(userId, request.roleName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/roles/{roleName}")
    public ResponseEntity<Void> removeRole(@PathVariable UUID userId, @PathVariable String roleName) {
        userService.removeRole(userId, roleName);
        return ResponseEntity.noContent().build();
    }

}
