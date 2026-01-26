package com.example.task_management_system.project.membership.dto;

import com.example.task_management_system.project.membership.ProjectRole;

import java.util.UUID;

public record AddMemberRequest(
        UUID userId,
        ProjectRole role
) {
}
