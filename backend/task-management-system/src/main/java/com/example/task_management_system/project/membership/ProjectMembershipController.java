package com.example.task_management_system.project.membership;

import com.example.task_management_system.project.membership.dto.AddMemberRequest;
import com.example.task_management_system.project.membership.dto.ChangeMemberRoleRequest;
import com.example.task_management_system.project.membership.dto.ProjectMemberResponse;
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
@RequestMapping("/api/projects/{projectId}/members")
public class ProjectMembershipController {

    private final ProjectMembershipService membershipService;

    public ProjectMembershipController(ProjectMembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectMemberResponse addMember(@PathVariable UUID projectId,
                                           @RequestBody AddMemberRequest request) {
        return membershipService.addMember(projectId, request);
    }

    @GetMapping
    public List<ProjectMemberResponse> listMembers(@PathVariable UUID projectId) {
        return membershipService.listMembers(projectId);
    }

    @PatchMapping("/{userId}/role")
    public ProjectMemberResponse changeRole(@PathVariable UUID projectId,
                                            @PathVariable UUID userId,
                                            @RequestBody ChangeMemberRoleRequest request) {
        return membershipService.changeRole(projectId, userId, request);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(@PathVariable UUID projectId, @PathVariable UUID userId) {
        membershipService.removeMember(projectId, userId);
    }
}
