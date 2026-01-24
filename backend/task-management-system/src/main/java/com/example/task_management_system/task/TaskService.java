package com.example.task_management_system.task;

import com.example.task_management_system.task.dto.ChangeAssigneeRequest;
import com.example.task_management_system.task.dto.ChangeStatusRequest;
import com.example.task_management_system.task.dto.TaskCreateRequest;
import com.example.task_management_system.task.dto.TaskResponse;
import com.example.task_management_system.task.dto.TaskUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    TaskResponse create(TaskCreateRequest request);

    TaskResponse getById(UUID taskId);

    List<TaskResponse> getByProject(UUID projectId);

    TaskResponse update(UUID taskId, TaskUpdateRequest request);

    TaskResponse changeStatus(UUID taskId, ChangeStatusRequest request);

    TaskResponse changeAssignee(UUID taskId, ChangeAssigneeRequest request);

    void delete(UUID taskId);
}
