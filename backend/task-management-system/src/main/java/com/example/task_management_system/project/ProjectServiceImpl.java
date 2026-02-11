package com.example.task_management_system.project;

import com.example.task_management_system.common.exception.ResourceNotFoundException;
import com.example.task_management_system.common.security.CurrentUserProvider;
import com.example.task_management_system.project.dto.ProjectCreateRequest;
import com.example.task_management_system.project.dto.ProjectResponse;
import com.example.task_management_system.project.dto.ProjectUpdateRequest;
import com.example.task_management_system.project.membership.ProjectMembership;
import com.example.task_management_system.project.membership.ProjectMembershipRepository;
import com.example.task_management_system.project.membership.ProjectRole;
import com.example.task_management_system.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMembershipRepository membershipRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public ProjectResponse create(ProjectCreateRequest request) {
        User currentUser = currentUserProvider.getCurrentUser();

        if (projectRepository.existsByKey(request.key())) {
            throw new IllegalArgumentException("Project key already exists");
        }

        Project project = new Project(
                request.key(),
                request.name(),
                request.description(),
                currentUser
        );

        projectRepository.save(project);

        ProjectMembership membership = new ProjectMembership(
                project,
                currentUser,
                ProjectRole.PROJECT_MANAGER
        );

        membershipRepository.save(membership);

        return mapToResponse(project);
    }

    @Override
    public ProjectResponse getById(UUID projectId) {
        Project project = getProjectOrThrow(projectId);
        User currentUser = currentUserProvider.getCurrentUser();

        if (!isMemberOrOwner(project, currentUser)) {
            throw new SecurityException("Access denied");
        }

        return mapToResponse(project);
    }

    @Override
    public List<ProjectResponse> getMyProjects() {
        User currentUser = currentUserProvider.getCurrentUser();

        return projectRepository.findAllByUser(currentUser.getId())
                                .stream()
                                .map(this::mapToResponse)
                                .toList();
    }

    @Override
    public ProjectResponse update(UUID projectId, ProjectUpdateRequest request) {
        Project project = getProjectOrThrow(projectId);
        User currentUser = currentUserProvider.getCurrentUser();
        System.out.println("can be updated " + project.canBeUpdatedBy(currentUser));
        if (!project.canBeUpdatedBy(currentUser)) {
            throw new AccessDeniedException("You are not allowed to update this project");
        }

        project.setName(request.name());
        project.setDescription(request.description());

        return mapToResponse(project);
    }

    @Override
    public void delete(UUID projectId) {
        Project project = getProjectOrThrow(projectId);
        User currentUser = currentUserProvider.getCurrentUser();

        boolean isOwner = project.getOwner().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.hasRole("ADMIN");

        if (!isOwner && !isAdmin) {
            throw new SecurityException("Only owner or admin can delete project");
        }

        projectRepository.delete(project);
    }

    private Project getProjectOrThrow(UUID id) {
        return projectRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    private boolean isMemberOrOwner(Project project, User user) {
        return project.getOwner().getId().equals(user.getId()) ||
               membershipRepository.existsByProjectIdAndUserId(project.getId(), user.getId());
    }

    private ProjectResponse mapToResponse(Project project) {
        User owner = project.getOwner();

        return new ProjectResponse(
                project.getId(),
                project.getKey(),
                project.getName(),
                project.getDescription(),
                owner.getId(),
                owner.getFirstName() + " " + owner.getLastName(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
