package com.example.task_management_system.task;

import com.example.task_management_system.task.dto.ChangeAssigneeRequest;
import com.example.task_management_system.task.dto.ChangeStatusRequest;
import com.example.task_management_system.task.dto.TaskCreateRequest;
import com.example.task_management_system.task.dto.TaskResponse;
import com.example.task_management_system.task.dto.TaskSearchCriteria;
import com.example.task_management_system.task.dto.TaskUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Create task", description = "Creates a new task in a project")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task created successfully",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Project/assignee not found",
                    content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@Valid @RequestBody TaskCreateRequest request) {
        return taskService.create(request);
    }

    @Operation(summary = "Get task by ID", description = "Returns task details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task returned successfully",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public TaskResponse getById(@PathVariable UUID id) {
        return taskService.getById(id);
    }


    @Operation(summary = "Search tasks for a project",
            description = "Returns filtered tasks for a given project")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks returned successfully",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
    })
    @GetMapping("/project/{projectId}")
    public List<TaskResponse> getByProject(@PathVariable UUID projectId, TaskSearchCriteria criteria) {
        return taskService.search(projectId, criteria);
    }

    @Operation(summary = "Update task", description = "Updates task fields")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task updated successfully",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content)
    })
    @PatchMapping("/{id}")
    public TaskResponse update(@PathVariable UUID id, @Valid @RequestBody TaskUpdateRequest request) {
        return taskService.update(id, request);
    }

    @Operation(summary = "Change task status", description = "Changes task status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task status changed successfully",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Task/status not found",
                    content = @Content)
    })
    @PatchMapping("/{id}/status")
    public TaskResponse changeStatus(@PathVariable UUID id, @Valid @RequestBody ChangeStatusRequest request) {
        return taskService.changeStatus(id, request);
    }

    @Operation(summary = "Change task assignee", description = "Assign/unassign a task")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task assignee changed successfully",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Task/assignee not found",
                    content = @Content)
    })
    @PatchMapping("/{id}/assignee")
    public TaskResponse changeAssignee(@PathVariable UUID id, @RequestBody ChangeAssigneeRequest request) {
        return taskService.changeAssignee(id, request);
    }

    @Operation(summary = "Delete task", description = "Deletes a task")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        taskService.delete(id);
    }
}
