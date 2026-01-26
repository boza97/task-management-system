package com.example.task_management_system.project.membership.dto;

import com.example.task_management_system.project.membership.ProjectRole;

public record ChangeMemberRoleRequest(
        ProjectRole role
) {
}