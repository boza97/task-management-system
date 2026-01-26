package com.example.task_management_system.project.membership.dto;

import com.example.task_management_system.project.membership.ProjectRole;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddMemberRequest(
        @NotNull UUID userId,
        @NotNull ProjectRole role
) {
}
