package com.example.task_management_system.project.membership;

import com.example.task_management_system.project.membership.dto.AddMemberRequest;
import com.example.task_management_system.project.membership.dto.ChangeMemberRoleRequest;
import com.example.task_management_system.project.membership.dto.ProjectMemberResponse;

import java.util.List;
import java.util.UUID;

public interface ProjectMembershipService {

    ProjectMemberResponse addMember(UUID projectId, AddMemberRequest request);

    List<ProjectMemberResponse> listMembers(UUID projectId);

    ProjectMemberResponse changeRole(UUID projectId, UUID userId, ChangeMemberRoleRequest request);

    void removeMember(UUID projectId, UUID userId);
}
