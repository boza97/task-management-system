package com.example.task_management_system.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, max = 100) String password,
        @NotBlank @Size(max = 50) String firstName,
        @NotBlank @Size(max = 50) String lastName
) {
}

