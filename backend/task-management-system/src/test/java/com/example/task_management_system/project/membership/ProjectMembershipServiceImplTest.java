package com.example.task_management_system.project.membership;

import com.example.task_management_system.common.exception.MemberHasAssignedTasksException;
import com.example.task_management_system.common.security.CurrentUserProvider;
import com.example.task_management_system.project.Project;
import com.example.task_management_system.project.ProjectRepository;
import com.example.task_management_system.project.membership.dto.AddMemberRequest;
import com.example.task_management_system.project.membership.dto.ChangeMemberRoleRequest;
import com.example.task_management_system.task.TaskRepository;
import com.example.task_management_system.user.User;
import com.example.task_management_system.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectMembershipServiceImplTest {

    @Mock
    private ProjectMembershipRepository membershipRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private ProjectMembershipServiceImpl membershipService;

    private User owner;
    private User member;
    private Project project;

    @BeforeEach
    void setUp() {
        owner = createUser("owner@example.com");
        member = createUser("member@example.com");
        project = new Project("TMS", "Task Management", "Description", owner);
        project.setId(UUID.randomUUID());
    }

    @Test
    void shouldAddMember() {
        AddMemberRequest request = new AddMemberRequest(member.getId(), ProjectRole.DEVELOPER);
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(currentUserProvider.getCurrentUser()).thenReturn(owner);
        when(userRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(membershipRepository.existsByProjectIdAndUserId(project.getId(), member.getId()))
                .thenReturn(false);
        when(membershipRepository.save(any(ProjectMembership.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = membershipService.addMember(project.getId(), request);

        assertThat(response.userId()).isEqualTo(member.getId());
        assertThat(response.role()).isEqualTo(ProjectRole.DEVELOPER);
        verify(membershipRepository).save(any(ProjectMembership.class));
    }

    @Test
    void shouldRejectDuplicateMember() {
        AddMemberRequest request = new AddMemberRequest(member.getId(), ProjectRole.QA);
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(currentUserProvider.getCurrentUser()).thenReturn(owner);
        when(userRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(membershipRepository.existsByProjectIdAndUserId(project.getId(), member.getId()))
                .thenReturn(true);

        assertThatThrownBy(() -> membershipService.addMember(project.getId(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User is already a member");

        verify(membershipRepository, never()).save(any(ProjectMembership.class));
    }

    @Test
    void shouldChangeMemberRole() {
        ProjectMembership membership =
                new ProjectMembership(project, member, ProjectRole.DEVELOPER);
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(currentUserProvider.getCurrentUser()).thenReturn(owner);
        when(membershipRepository.findByProjectIdAndUserId(project.getId(), member.getId()))
                .thenReturn(Optional.of(membership));

        var response = membershipService.changeRole(
                project.getId(),
                member.getId(),
                new ChangeMemberRoleRequest(ProjectRole.QA)
        );

        assertThat(response.role()).isEqualTo(ProjectRole.QA);
        verify(membershipRepository).save(membership);
    }

    @Test
    void shouldRejectRoleChangeByNonOwner() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(currentUserProvider.getCurrentUser()).thenReturn(member);

        assertThatThrownBy(() -> membershipService.changeRole(
                project.getId(),
                member.getId(),
                new ChangeMemberRoleRequest(ProjectRole.QA)
        )).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void shouldListProjectMembers() {
        ProjectMembership membership =
                new ProjectMembership(project, member, ProjectRole.DEVELOPER);
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(membershipRepository.findAllByProjectId(project.getId()))
                .thenReturn(List.of(membership));

        var result = membershipService.listMembers(project.getId());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().email()).isEqualTo(member.getEmail());
    }

    @Test
    void shouldNotRemoveMemberWithAssignedTasks() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(currentUserProvider.getCurrentUser()).thenReturn(owner);
        when(taskRepository.existsByProjectIdAndAssigneeId(project.getId(), member.getId()))
                .thenReturn(true);

        assertThatThrownBy(() -> membershipService.removeMember(project.getId(), member.getId()))
                .isInstanceOf(MemberHasAssignedTasksException.class);

        verify(membershipRepository, never())
                .deleteByProjectIdAndUserId(project.getId(), member.getId());
    }

    private User createUser(String email) {
        User user = new User("Test", "User", email, "password");
        user.setId(UUID.randomUUID());
        return user;
    }
}
