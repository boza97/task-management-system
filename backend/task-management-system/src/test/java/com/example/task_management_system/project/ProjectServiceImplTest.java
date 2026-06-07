package com.example.task_management_system.project;

import com.example.task_management_system.common.exception.ResourceNotFoundException;
import com.example.task_management_system.common.security.CurrentUserProvider;
import com.example.task_management_system.project.dto.ProjectCreateRequest;
import com.example.task_management_system.project.dto.ProjectUpdateRequest;
import com.example.task_management_system.project.membership.ProjectMembership;
import com.example.task_management_system.project.membership.ProjectMembershipRepository;
import com.example.task_management_system.project.membership.ProjectRole;
import com.example.task_management_system.user.Role;
import com.example.task_management_system.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMembershipRepository membershipRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private User user;

    @BeforeEach
    void setUp() {
        user = createUser();
        lenient().when(currentUserProvider.getCurrentUser()).thenReturn(user);
    }

    @Test
    void shouldCreateProject() {
        ProjectCreateRequest request = new ProjectCreateRequest("KEY", "Name", "Desc");
        when(projectRepository.existsByKey("KEY")).thenReturn(false);

        var response = projectService.create(request);

        verify(projectRepository).save(any(Project.class));
        ArgumentCaptor<ProjectMembership> membershipCaptor =
                ArgumentCaptor.forClass(ProjectMembership.class);
        verify(membershipRepository).save(membershipCaptor.capture());

        assertThat(response.key()).isEqualTo("KEY");
        assertThat(response.ownerId()).isEqualTo(user.getId());
        assertThat(membershipCaptor.getValue().getUser()).isEqualTo(user);
        assertThat(membershipCaptor.getValue().getRole()).isEqualTo(ProjectRole.PROJECT_MANAGER);
    }

    @Test
    void shouldThrowWhenKeyExists() {
        ProjectCreateRequest request = new ProjectCreateRequest("KEY", "Name", "Desc");
        when(projectRepository.existsByKey("KEY")).thenReturn(true);

        assertThatThrownBy(() -> projectService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Project key already exists");

        verify(projectRepository, never()).save(any(Project.class));
        verify(membershipRepository, never()).save(any(ProjectMembership.class));
    }

    @Test
    void shouldGetProjectWhenUserIsOwner() {
        Project project = new Project("KEY", "Name", "Desc", user);
        project.setId(UUID.randomUUID());

        when(projectRepository.findById(project.getId()))
                .thenReturn(Optional.of(project));

        var response = projectService.getById(project.getId());

        assertThat(response.key()).isEqualTo("KEY");
    }

    @Test
    void shouldGetProjectWhenUserIsMember() {
        User owner = createUser();
        Project project = new Project("KEY", "Name", "Desc", owner);
        project.setId(UUID.randomUUID());

        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(membershipRepository.existsByProjectIdAndUserId(project.getId(), user.getId()))
                .thenReturn(true);

        var response = projectService.getById(project.getId());

        assertThat(response.id()).isEqualTo(project.getId());
        assertThat(response.ownerId()).isEqualTo(owner.getId());
    }

    @Test
    void shouldRejectProjectAccessWhenUserIsNotMember() {
        User owner = createUser();
        Project project = new Project("KEY", "Name", "Desc", owner);
        project.setId(UUID.randomUUID());

        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(membershipRepository.existsByProjectIdAndUserId(project.getId(), user.getId()))
                .thenReturn(false);

        assertThatThrownBy(() -> projectService.getById(project.getId()))
                .isInstanceOf(SecurityException.class)
                .hasMessage("Access denied");
    }

    @Test
    void shouldThrowWhenProjectNotFound() {
        UUID id = UUID.randomUUID();

        when(projectRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldReturnProjectsForCurrentUser() {
        Project firstProject = new Project("ONE", "First", "Desc", user);
        Project secondProject = new Project("TWO", "Second", "Desc", user);
        when(projectRepository.findAllByUser(user.getId()))
                .thenReturn(List.of(firstProject, secondProject));

        var response = projectService.getMyProjects();

        assertThat(response)
                .extracting(project -> project.key())
                .containsExactly("ONE", "TWO");
    }

    @Test
    void shouldUpdateProject() {
        Project project = spy(new Project("KEY", "Name", "Desc", user));
        project.setId(UUID.randomUUID());

        when(projectRepository.findById(project.getId()))
                .thenReturn(Optional.of(project));

        doReturn(true).when(project).canBeUpdatedBy(user);

        ProjectUpdateRequest request = new ProjectUpdateRequest("New", "NewDesc");

        var response = projectService.update(project.getId(), request);

        assertThat(response.name()).isEqualTo("New");
        assertThat(response.description()).isEqualTo("NewDesc");
    }

    @Test
    void shouldThrowWhenUpdateNotAllowed() {
        Project project = spy(new Project("KEY", "Name", "Desc", user));
        project.setId(UUID.randomUUID());

        when(projectRepository.findById(project.getId()))
                .thenReturn(Optional.of(project));

        doReturn(false).when(project).canBeUpdatedBy(user);

        ProjectUpdateRequest request = new ProjectUpdateRequest("New", "NewDesc");

        assertThatThrownBy(() -> projectService.update(project.getId(), request))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You are not allowed to update this project");
    }

    @Test
    void shouldDeleteProjectWhenOwner() {
        Project project = new Project("KEY", "Name", "Desc", user);
        project.setId(UUID.randomUUID());

        when(projectRepository.findById(project.getId()))
                .thenReturn(Optional.of(project));

        projectService.delete(project.getId());

        verify(projectRepository).delete(project);
    }

    @Test
    void shouldDeleteProjectWhenUserIsAdmin() {
        User owner = createUser();
        user.getRoles().add(new Role("ADMIN"));
        Project project = new Project("KEY", "Name", "Desc", owner);
        project.setId(UUID.randomUUID());

        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

        projectService.delete(project.getId());

        verify(projectRepository).delete(project);
    }

    @Test
    void shouldThrowWhenDeleteNotAllowed() {
        User other = createUser();
        Project project = new Project("KEY", "Name", "Desc", other);
        project.setId(UUID.randomUUID());

        when(projectRepository.findById(project.getId()))
                .thenReturn(Optional.of(project));

        assertThatThrownBy(() -> projectService.delete(project.getId()))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Only owner or admin can delete project");

        verify(projectRepository, never()).delete(any(Project.class));
    }

    private User createUser() {
        try {
            var constructor = User.class.getDeclaredConstructor();
            constructor.setAccessible(true);

            User user = constructor.newInstance();
            user.setId(UUID.randomUUID());
            user.setFirstName("Test");
            user.setLastName("User");

            return user;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not create test user", exception);
        }
    }
}
