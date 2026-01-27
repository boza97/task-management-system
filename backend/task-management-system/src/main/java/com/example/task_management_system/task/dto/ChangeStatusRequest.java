package com.example.task_management_system.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "ChangeStatusRequest", description = "Request payload to change task status")
public record ChangeStatusRequest(
        @Schema(example = "IN_DEVELOPMENT", description = "New TaskStatus")
        @NotBlank
        String statusCode
) {
}
