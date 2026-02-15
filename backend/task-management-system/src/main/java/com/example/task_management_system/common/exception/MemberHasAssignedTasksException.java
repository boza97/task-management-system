package com.example.task_management_system.common.exception;

public class MemberHasAssignedTasksException extends RuntimeException {
    public MemberHasAssignedTasksException(String message) {
        super(message);
    }
}
