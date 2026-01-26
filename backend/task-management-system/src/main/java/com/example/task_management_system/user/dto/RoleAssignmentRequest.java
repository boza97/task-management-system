package com.example.task_management_system.user.dto;

import jakarta.validation.constraints.NotNull;


public record RoleAssignmentRequest(
        @NotNull String roleName
) {
}
