package com.example.task_management_system.task.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentCreateRequest(
        @NotBlank @Size(max = 2000) String content
) {
}
