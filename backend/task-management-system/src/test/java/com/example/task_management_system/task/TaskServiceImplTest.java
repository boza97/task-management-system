package com.example.task_management_system.task;

import com.example.task_management_system.common.security.CurrentUserProvider;
import com.example.task_management_system.project.Project;
import com.example.task_management_system.project.ProjectRepository;
import com.example.task_management_system.project.membership.ProjectMembershipRepository;
import com.example.task_management_system.task.audit.AuditLogRepository;
import com.example.task_management_system.task.dto.TaskCreateRequest;
import com.example.task_management_system.task.status.TaskStatus;
import com.example.task_management_system.task.status.TaskStatusRepository;
import com.example.task_management_system.user.User;
import com.example.task_management_system.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private AuditLogRepository auditLogRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TaskStatusRepository taskStatusRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CurrentUserProvider currentUserProvider;
    @Mock
    private ProjectMembershipRepository membershipRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    void shouldCreateTask() {
        UUID projectId = UUID.randomUUID();

        User user = createUser();
        user.setId(UUID.randomUUID());

        Project project = new Project();
        project.setId(projectId);

        TaskStatus status = new TaskStatus();
        status.setCode("TODO");

        TaskCreateRequest request = new TaskCreateRequest(
                projectId,
                "Test title",
                "Desc",
                null,
                null,
                null
        );

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskStatusRepository.findByCode("TODO")).thenReturn(Optional.of(status));
        when(taskRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = taskService.create(request);

        assertNotNull(result);
        assertEquals("Test title", result.title());

        verify(taskRepository).save(any());
        verify(auditLogRepository).save(any());
    }

    @Test
    void shouldChangeStatus() {
        UUID taskId = UUID.randomUUID();

        Task task = new Task();
        TaskStatus oldStatus = new TaskStatus();
        oldStatus.setCode("TODO");

        TaskStatus newStatus = new TaskStatus();
        newStatus.setCode("DONE");

        Project project = new Project();
        project.setId(UUID.randomUUID());

        User user = createUser();
        user.setId(UUID.randomUUID());

        task.setStatus(oldStatus);
        task.setProject(project);
        task.setCreatedBy(user);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskStatusRepository.findByCode("DONE")).thenReturn(Optional.of(newStatus));
        when(currentUserProvider.getCurrentUser()).thenReturn(createUser());

        var result = taskService.changeStatus(taskId,
                                              new com.example.task_management_system.task.dto.ChangeStatusRequest(
                                                      "DONE"));

        assertEquals("DONE", task.getStatus().getCode());
        verify(auditLogRepository).save(any());
    }

    @Test
    void shouldThrowWhenUserCannotDelete() {
        UUID taskId = UUID.randomUUID();

        User user = createUser();
        user.setId(UUID.randomUUID());

        User creator = createUser();
        creator.setId(UUID.randomUUID());

        Project project = new Project();
        project.setId(UUID.randomUUID());

        Task task = new Task();
        task.setCreatedBy(creator);
        task.setProject(project);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(membershipRepository.existsByProjectIdAndUserIdAndRole(any(), any(), any()))
                .thenReturn(false);

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                     () -> taskService.delete(taskId));
    }

    @Test
    void shouldReturnTaskById() {
        UUID id = UUID.randomUUID();

        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setCode("TO_DO");
        taskStatus.setName("TO DO");

        Project project = new Project();
        project.setId(UUID.randomUUID());

        User user = createUser();
        user.setId(UUID.randomUUID());

        Task task = new Task();
        task.setId(id);
        task.setStatus(taskStatus);
        task.setProject(project);
        task.setCreatedBy(user);


        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        var result = taskService.getById(id);

        assertEquals(id, result.id());
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
