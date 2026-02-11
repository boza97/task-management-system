package com.example.task_management_system.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "ProjectUpdateRequest", description = "Payload for updating project")
public record ProjectUpdateRequest(

        @Schema(example = "Task Management System v2", description = "Updated project name")
        @NotBlank
        @Size(max = 100)
        String name,

        @Schema(example = "Updated description", description = "Updated project description", maxLength = 1000)
        @Size(max = 1000)
        String description
) {
}
