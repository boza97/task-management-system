package com.example.task_management_system.task;

import com.example.task_management_system.task.dto.ChangeAssigneeRequest;
import com.example.task_management_system.task.dto.ChangeStatusRequest;
import com.example.task_management_system.task.dto.TaskCreateRequest;
import com.example.task_management_system.task.dto.TaskResponse;
import com.example.task_management_system.task.dto.TaskUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@Valid @RequestBody TaskCreateRequest request) {
        return taskService.create(request);
    }

    @GetMapping("/{id}")
    public TaskResponse getById(@PathVariable UUID id) {
        return taskService.getById(id);
    }

    @GetMapping("/project/{projectId}")
    public List<TaskResponse> getByProject(@PathVariable UUID projectId) {
        return taskService.getByProject(projectId);
    }

    @PatchMapping("/{id}")
    public TaskResponse update(@PathVariable UUID id, @Valid @RequestBody TaskUpdateRequest request) {
        return taskService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public TaskResponse changeStatus(@PathVariable UUID id, @Valid @RequestBody ChangeStatusRequest request) {
        return taskService.changeStatus(id, request);
    }

    @PatchMapping("/{id}/assignee")
    public TaskResponse changeAssignee(@PathVariable UUID id, @RequestBody ChangeAssigneeRequest request) {
        return taskService.changeAssignee(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        taskService.delete(id);
    }
}
