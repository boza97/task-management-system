package com.example.task_management_system.project.membership.dto;

import com.example.task_management_system.project.membership.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record ChangeMemberRoleRequest(
        @NotNull ProjectRole role
) {
}