package com.example.task_management_system.task.dto;

import java.util.UUID;

public record ChangeAssigneeRequest(
        UUID assigneeId
) {
}
