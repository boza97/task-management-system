package com.example.task_management_system.project;

import com.example.task_management_system.project.dto.ProjectCreateRequest;
import com.example.task_management_system.project.dto.ProjectResponse;
import com.example.task_management_system.project.dto.ProjectUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ProjectService {
    ProjectResponse create(ProjectCreateRequest request);

    ProjectResponse getById(UUID projectId);

    List<ProjectResponse> getMyProjects();

    ProjectResponse update(UUID projectId, ProjectUpdateRequest request);

    void delete(UUID projectId);
}
