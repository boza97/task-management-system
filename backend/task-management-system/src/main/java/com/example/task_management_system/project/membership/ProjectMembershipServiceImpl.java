package com.example.task_management_system.project.membership;

import com.example.task_management_system.common.exception.ResourceNotFoundException;
import com.example.task_management_system.common.security.CurrentUserProvider;
import com.example.task_management_system.project.Project;
import com.example.task_management_system.project.ProjectRepository;
import com.example.task_management_system.project.membership.dto.AddMemberRequest;
import com.example.task_management_system.project.membership.dto.ChangeMemberRoleRequest;
import com.example.task_management_system.project.membership.dto.ProjectMemberResponse;
import com.example.task_management_system.user.User;
import com.example.task_management_system.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectMembershipServiceImpl implements ProjectMembershipService {

    private final ProjectMembershipRepository membershipRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public ProjectMemberResponse addMember(UUID projectId, AddMemberRequest request) {
        Project project = getProjectOrThrow(projectId);
        User currentUser = currentUserProvider.getCurrentUser();

        if (!project.canBeUpdatedBy(currentUser)) {
            throw new AccessDeniedException("Not allowed to add members");
        }

        User member = userRepository.findById(request.userId())
                                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (membershipRepository.existsByProjectIdAndUserId(projectId, member.getId())) {
            throw new IllegalArgumentException("User is already a member");
        }

        ProjectMembership membership = new ProjectMembership();
        membership.setProject(project);
        membership.setUser(member);
        membership.setRole(request.role());

        membership = membershipRepository.save(membership);

        return mapToResponse(membership);
    }

    @Override
    public List<ProjectMemberResponse> listMembers(UUID projectId) {
        getProjectOrThrow(projectId);

        return membershipRepository.findAllByProjectId(projectId).stream()
                                   .map(this::mapToResponse)
                                   .toList();
    }

    @Override
    public ProjectMemberResponse changeRole(UUID projectId, UUID userId, ChangeMemberRoleRequest request) {
        Project project = getProjectOrThrow(projectId);
        User currentUser = currentUserProvider.getCurrentUser();

        if (!project.isOwner(currentUser)) {
            throw new AccessDeniedException("Only owner can change roles");
        }

        ProjectMembership membership = membershipRepository.findByProjectIdAndUserId(projectId, userId)
                                                           .orElseThrow(() -> new ResourceNotFoundException(
                                                                   "Membership not found"));

        membership.setRole(request.role());
        membershipRepository.save(membership);

        return mapToResponse(membership);
    }

    @Override
    public void removeMember(UUID projectId, UUID userId) {
        Project project = getProjectOrThrow(projectId);
        User currentUser = currentUserProvider.getCurrentUser();

        if (!project.canBeUpdatedBy(currentUser)) {
            throw new AccessDeniedException("Not allowed to remove members");
        }

        if (project.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Cannot remove project owner");
        }

        membershipRepository.deleteByProjectIdAndUserId(projectId, userId);
    }

    private Project getProjectOrThrow(UUID projectId) {
        return projectRepository.findById(projectId)
                                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    private ProjectMemberResponse mapToResponse(ProjectMembership membership) {
        User u = membership.getUser();

        return new ProjectMemberResponse(
                u.getId(),
                u.getEmail(),
                u.getFirstName(),
                u.getLastName(),
                membership.getRole(),
                membership.getJoinedAt()
        );
    }
}
