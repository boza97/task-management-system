package com.example.task_management_system.project;

import com.example.task_management_system.project.dto.ProjectCreateRequest;
import com.example.task_management_system.project.dto.ProjectResponse;
import com.example.task_management_system.project.dto.ProjectUpdateRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ProjectResponse create(@RequestBody ProjectCreateRequest request) {
        return projectService.create(request);
    }

    @GetMapping("/{id}")
    public ProjectResponse getById(@PathVariable UUID id) {
        return projectService.getById(id);
    }

    @GetMapping("/my")
    public List<ProjectResponse> getMyProjects() {
        return projectService.getMyProjects();
    }

    @PutMapping("/{id}")
    public ProjectResponse update(@PathVariable UUID id,
                                  @RequestBody ProjectUpdateRequest request) {
        return projectService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        projectService.delete(id);
    }
}
