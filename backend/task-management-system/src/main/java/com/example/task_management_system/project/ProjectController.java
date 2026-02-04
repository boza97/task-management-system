package com.example.task_management_system.project;

import com.example.task_management_system.project.dto.ProjectCreateRequest;
import com.example.task_management_system.project.dto.ProjectResponse;
import com.example.task_management_system.project.dto.ProjectUpdateRequest;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @Operation(
            summary = "Create project",
            description = "Creates a new project. The current authenticated user becomes the project owner."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Project created successfully",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized (missing/invalid JWT)",
                    content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(@Valid @RequestBody ProjectCreateRequest request) {
        return projectService.create(request);
    }

    @Operation(
            summary = "Get project by ID",
            description = "Returns project details. User must be a project member or owner."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project returned successfully",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden (user not a member/owner)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ProjectResponse getById(@PathVariable UUID id) {
        return projectService.getById(id);
    }

    @Operation(
            summary = "Get my projects",
            description = "Returns all projects where the current authenticated user is owner or member."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projects returned successfully",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content)
    })
    @GetMapping("/my")
    public List<ProjectResponse> getMyProjects() {
        return projectService.getMyProjects();
    }

    @Operation(
            summary = "Update project",
            description = "Updates project name and description. Only the project owner can update the project."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project updated successfully",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden (only project owner can update)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ProjectResponse update(@PathVariable UUID id,
                                  @Valid @RequestBody ProjectUpdateRequest request) {
        return projectService.update(id, request);
    }

    @Operation(
            summary = "Delete project",
            description = "Deletes a project. Only the project owner can delete the project."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Project deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden (only project owner can delete)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        projectService.delete(id);
    }
}
