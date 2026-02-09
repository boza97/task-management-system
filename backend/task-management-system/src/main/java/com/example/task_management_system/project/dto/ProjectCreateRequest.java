package com.example.task_management_system.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "ProjectCreateRequest", description = "Payload for creating a new project")
public record ProjectCreateRequest(

        @Schema(example = "TMS", description = "Unique project key (max 10 chars). Usually uppercase.", maxLength = 10)
        @NotBlank
        @Size(max = 10)
        String key,

        @Schema(example = "Task Management System", description = "Project name", maxLength = 100)
        @NotBlank
        @Size(max = 100)
        String name,

        @Schema(example = "Task management system built with Spring Boot and Angular", description = "Project description", maxLength = 1000)
        @Size(max = 1000)
        String description
) {
}
