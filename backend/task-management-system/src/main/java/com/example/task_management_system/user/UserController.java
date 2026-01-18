package com.example.task_management_system.user;

import com.example.task_management_system.user.dto.RoleAssignmentRequest;
import com.example.task_management_system.user.dto.UserRegistrationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody UserRegistrationRequest request) {
        userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{userId}/roles")
    public ResponseEntity<Void> assignRole(@PathVariable UUID userId, @RequestBody RoleAssignmentRequest request) {
        userService.assignRole(userId, request.getRoleName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/roles/{roleName}")
    public ResponseEntity<Void> removeRole(@PathVariable UUID userId, @PathVariable String roleName) {
        userService.removeRole(userId, roleName);
        return ResponseEntity.noContent().build();
    }

}
