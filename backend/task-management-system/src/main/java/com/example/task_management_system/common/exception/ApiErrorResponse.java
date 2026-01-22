package com.example.task_management_system.common.exception;

public record ApiErrorResponse(
        int status,
        String message
) {
}