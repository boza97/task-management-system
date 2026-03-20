package com.example.task_management_system.project;

import com.example.task_management_system.common.exception.ResourceNotFoundException;
import com.example.task_management_system.common.security.CurrentUserProvider;
import com.example.task_management_system.project.dto.ProjectCreateRequest;
import com.example.task_management_system.project.dto.ProjectUpdateRequest;
import com.example.task_management_system.project.membership.ProjectMembershipRepository;
import com.example.task_management_system.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        MockitoAnnotations.openMocks(this);
        user = createUser();
        when(currentUserProvider.getCurrentUser()).thenReturn(user);
    }

    @Test
    void shouldCreateProject() {
        ProjectCreateRequest request = new ProjectCreateRequest("KEY", "Name", "Desc");
        when(projectRepository.existsByKey("KEY")).thenReturn(false);

        var response = projectService.create(request);

        verify(projectRepository).save(any(Project.class));
        verify(membershipRepository).save(any());
        assertThat(response.key()).isEqualTo("KEY");
    }

    @Test
    void shouldThrowWhenKeyExists() {
        ProjectCreateRequest request = new ProjectCreateRequest("KEY", "Name", "Desc");
        when(projectRepository.existsByKey("KEY")).thenReturn(true);

        assertThatThrownBy(() -> projectService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
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
    void shouldThrowWhenProjectNotFound() {
        UUID id = UUID.randomUUID();

        when(projectRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getById(id))
                .isInstanceOf(ResourceNotFoundException.class);
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
                .isInstanceOf(AccessDeniedException.class);
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
    void shouldThrowWhenDeleteNotAllowed() {
        User other = createUser();
        Project project = new Project("KEY", "Name", "Desc", other);
        project.setId(UUID.randomUUID());

        when(projectRepository.findById(project.getId()))
                .thenReturn(Optional.of(project));

        assertThatThrownBy(() -> projectService.delete(project.getId()))
                .isInstanceOf(AccessDeniedException.class);
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}