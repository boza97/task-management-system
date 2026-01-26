package com.example.task_management_system.project.membership.dto;

import com.example.task_management_system.project.membership.ProjectRole;

import java.time.Instant;
import java.util.UUID;

public record ProjectMemberResponse(
        UUID userId,
        String email,
        String firstName,
        String lastName,
        ProjectRole role,
        Instant joinedAt
) {
}
