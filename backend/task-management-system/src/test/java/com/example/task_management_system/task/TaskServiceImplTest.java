package com.example.task_management_system.task;

import com.example.task_management_system.common.exception.ResourceNotFoundException;
import com.example.task_management_system.common.security.CurrentUserProvider;
import com.example.task_management_system.notification.NotificationService;
import com.example.task_management_system.project.Project;
import com.example.task_management_system.project.ProjectRepository;
import com.example.task_management_system.project.membership.ProjectMembershipRepository;
import com.example.task_management_system.project.membership.ProjectRole;
import com.example.task_management_system.task.audit.ActionType;
import com.example.task_management_system.task.audit.AuditLog;
import com.example.task_management_system.task.audit.AuditLogRepository;
import com.example.task_management_system.task.dto.ChangeAssigneeRequest;
import com.example.task_management_system.task.dto.ChangeStatusRequest;
import com.example.task_management_system.task.dto.TaskCreateRequest;
import com.example.task_management_system.task.dto.TaskSearchCriteria;
import com.example.task_management_system.task.dto.TaskUpdateRequest;
import com.example.task_management_system.task.status.TaskStatus;
import com.example.task_management_system.task.status.TaskStatusRepository;
import com.example.task_management_system.user.Role;
import com.example.task_management_system.user.User;
import com.example.task_management_system.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    void shouldCreateTaskAndAuditCreation() {
        UUID projectId = UUID.randomUUID();
        User creator = createUser("Test", "Creator");
        Project project = createProject(projectId);
        TaskStatus defaultStatus = createStatus("TODO", "To do");
        TaskCreateRequest request = new TaskCreateRequest(
                projectId,
                "Test title",
                "Description",
                TaskPriority.HIGH,
                LocalDate.of(2026, 6, 30),
                null
        );

        when(currentUserProvider.getCurrentUser()).thenReturn(creator);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskStatusRepository.findByCode("TODO")).thenReturn(Optional.of(defaultStatus));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = taskService.create(request);

        assertEquals("Test title", response.title());
        assertEquals(TaskPriority.HIGH, response.priority());
        assertEquals("TODO", response.statusCode());
        assertEquals(projectId, response.projectId());
        assertEquals(creator.getId(), response.createdById());

        ArgumentCaptor<AuditLog> auditCaptor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(auditCaptor.capture());
        assertEquals(ActionType.TASK_CREATED, auditCaptor.getValue().getActionType());
        assertSame(creator, auditCaptor.getValue().getPerformedBy());
    }

    @Test
    void shouldAssignUserWhenCreatingTask() {
        UUID projectId = UUID.randomUUID();
        User creator = createUser("Test", "Creator");
        User assignee = createUser("Test", "Assignee");
        Project project = createProject(projectId);
        TaskStatus defaultStatus = createStatus("TODO", "To do");
        TaskCreateRequest request = new TaskCreateRequest(
                projectId,
                "Assigned task",
                null,
                TaskPriority.MEDIUM,
                null,
                assignee.getId()
        );

        when(currentUserProvider.getCurrentUser()).thenReturn(creator);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskStatusRepository.findByCode("TODO")).thenReturn(Optional.of(defaultStatus));
        when(userRepository.findById(assignee.getId())).thenReturn(Optional.of(assignee));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = taskService.create(request);

        assertEquals(assignee.getId(), response.assigneeId());
        assertEquals("Test Assignee", response.assigneeFullName());
    }

    @Test
    void shouldRejectTaskCreationWhenProjectDoesNotExist() {
        UUID projectId = UUID.randomUUID();
        TaskCreateRequest request = new TaskCreateRequest(
                projectId,
                "Task",
                null,
                TaskPriority.LOW,
                null,
                null
        );

        when(currentUserProvider.getCurrentUser()).thenReturn(createUser("Test", "Creator"));
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.create(request));
        verify(taskRepository, never()).save(any(Task.class));
        verify(auditLogRepository, never()).save(any(AuditLog.class));
    }

    @Test
    void shouldRejectTaskCreationWhenAssigneeDoesNotExist() {
        UUID projectId = UUID.randomUUID();
        UUID assigneeId = UUID.randomUUID();
        TaskCreateRequest request = new TaskCreateRequest(
                projectId,
                "Task",
                null,
                TaskPriority.LOW,
                null,
                assigneeId
        );

        when(currentUserProvider.getCurrentUser()).thenReturn(createUser("Test", "Creator"));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProject(projectId)));
        when(taskStatusRepository.findByCode("TODO")).thenReturn(Optional.of(createStatus("TODO", "To do")));
        when(userRepository.findById(assigneeId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.create(request));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldRejectTaskCreationWhenDefaultStatusIsMissing() {
        UUID projectId = UUID.randomUUID();
        TaskCreateRequest request = new TaskCreateRequest(
                projectId,
                "Task",
                null,
                TaskPriority.LOW,
                null,
                null
        );

        when(currentUserProvider.getCurrentUser()).thenReturn(createUser("Test", "Creator"));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProject(projectId)));
        when(taskStatusRepository.findByCode("TODO")).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> taskService.create(request)
        );

        assertEquals("Default status TODO missing", exception.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
        verify(auditLogRepository, never()).save(any(AuditLog.class));
    }

    @Test
    void shouldReturnTaskById() {
        Task task = createTask();
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        var response = taskService.getById(task.getId());

        assertEquals(task.getId(), response.id());
        assertEquals(task.getTitle(), response.title());
        assertEquals(task.getProject().getId(), response.projectId());
    }

    @Test
    void shouldThrowWhenTaskDoesNotExist() {
        UUID taskId = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getById(taskId));
    }

    @Test
    void shouldReturnTasksForProjectWhenSearchCriteriaAreMissing() {
        Task task = createTask();
        UUID projectId = task.getProject().getId();
        when(taskRepository.findAllByProjectIdOrderByUpdatedAtDesc(projectId))
                .thenReturn(List.of(task));

        var responses = taskService.search(projectId, null);

        assertEquals(1, responses.size());
        assertEquals(task.getId(), responses.getFirst().id());
    }

    @Test
    void shouldSearchTasksUsingProvidedFilters() {
        Task task = createTask();
        UUID projectId = task.getProject().getId();
        TaskSearchCriteria criteria = new TaskSearchCriteria(
                "test",
                TaskPriority.HIGH,
                "TODO",
                task.getCreatedBy().getId(),
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30)
        );
        when(taskRepository.findAll(any(Specification.class))).thenReturn(List.of(task));

        var responses = taskService.search(projectId, criteria);

        assertEquals(1, responses.size());
        assertEquals(task.getId(), responses.getFirst().id());
        verify(taskRepository).findAll(any(Specification.class));
        verify(taskRepository, never()).findAllByProjectIdOrderByUpdatedAtDesc(projectId);
    }

    @Test
    void shouldUpdateChangedFieldsAndCreateAuditLogs() {
        Task task = createTask();
        User currentUser = createUser("Current", "User");
        TaskUpdateRequest request = new TaskUpdateRequest(
                "Updated title",
                "Updated description",
                TaskPriority.CRITICAL,
                LocalDate.of(2026, 7, 15)
        );
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(currentUserProvider.getCurrentUser()).thenReturn(currentUser);

        var response = taskService.update(task.getId(), request);

        assertEquals("Updated title", response.title());
        assertEquals("Updated description", response.description());
        assertEquals(TaskPriority.CRITICAL, response.priority());
        assertEquals(LocalDate.of(2026, 7, 15), response.dueDate());

        ArgumentCaptor<AuditLog> auditCaptor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository, times(4)).save(auditCaptor.capture());
        assertEquals(
                List.of(
                        ActionType.TITLE_CHANGED,
                        ActionType.DESCRIPTION_CHANGED,
                        ActionType.DUE_DATE_CHANGED,
                        ActionType.PRIORITY_CHANGED
                ),
                auditCaptor.getAllValues().stream().map(AuditLog::getActionType).toList()
        );
    }

    @Test
    void shouldNotCreateAuditLogsWhenTaskDataAreUnchanged() {
        Task task = createTask();
        TaskUpdateRequest request = new TaskUpdateRequest(
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getDueDate()
        );
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(currentUserProvider.getCurrentUser()).thenReturn(createUser("Current", "User"));

        taskService.update(task.getId(), request);

        verify(auditLogRepository, never()).save(any(AuditLog.class));
    }

    @Test
    void shouldChangeStatusAndCreateAuditLog() {
        Task task = createTask();
        TaskStatus newStatus = createStatus("DONE", "Done");
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(taskStatusRepository.findByCode("DONE")).thenReturn(Optional.of(newStatus));
        when(currentUserProvider.getCurrentUser()).thenReturn(createUser("Current", "User"));

        var response = taskService.changeStatus(task.getId(), new ChangeStatusRequest("DONE"));

        assertEquals("DONE", response.statusCode());
        ArgumentCaptor<AuditLog> auditCaptor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(auditCaptor.capture());
        assertEquals(ActionType.STATUS_CHANGED, auditCaptor.getValue().getActionType());
        assertEquals("TODO", auditCaptor.getValue().getOldValue());
        assertEquals("DONE", auditCaptor.getValue().getNewValue());
    }

    @Test
    void shouldNotCreateAuditLogWhenStatusIsUnchanged() {
        Task task = createTask();
        TaskStatus sameStatus = createStatus("TODO", "To do");
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(taskStatusRepository.findByCode("TODO")).thenReturn(Optional.of(sameStatus));
        when(currentUserProvider.getCurrentUser()).thenReturn(createUser("Current", "User"));

        taskService.changeStatus(task.getId(), new ChangeStatusRequest("TODO"));

        verify(auditLogRepository, never()).save(any(AuditLog.class));
    }

    @Test
    void shouldThrowWhenNewStatusDoesNotExist() {
        Task task = createTask();
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(currentUserProvider.getCurrentUser()).thenReturn(createUser("Current", "User"));
        when(taskStatusRepository.findByCode("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.changeStatus(task.getId(), new ChangeStatusRequest("UNKNOWN"))
        );
        verify(auditLogRepository, never()).save(any(AuditLog.class));
    }

    @Test
    void shouldAssignUserAndCreateAuditLog() {
        Task task = createTask();
        User assignee = createUser("New", "Assignee");
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(currentUserProvider.getCurrentUser()).thenReturn(createUser("Current", "User"));
        when(userRepository.findById(assignee.getId())).thenReturn(Optional.of(assignee));

        var response = taskService.changeAssignee(
                task.getId(),
                new ChangeAssigneeRequest(assignee.getId())
        );

        assertEquals(assignee.getId(), response.assigneeId());
        ArgumentCaptor<AuditLog> auditCaptor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(auditCaptor.capture());
        assertEquals(ActionType.ASSIGNEE_CHANGED, auditCaptor.getValue().getActionType());
        assertEquals("Unassigned", auditCaptor.getValue().getOldValue());
        assertEquals("New Assignee", auditCaptor.getValue().getNewValue());
    }

    @Test
    void shouldUnassignUserAndCreateAuditLog() {
        Task task = createTask();
        User assignee = createUser("Old", "Assignee");
        task.setAssignee(assignee);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(currentUserProvider.getCurrentUser()).thenReturn(createUser("Current", "User"));

        var response = taskService.changeAssignee(task.getId(), new ChangeAssigneeRequest(null));

        assertNull(response.assigneeId());
        ArgumentCaptor<AuditLog> auditCaptor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(auditCaptor.capture());
        assertEquals("Old Assignee", auditCaptor.getValue().getOldValue());
        assertEquals("Unassigned", auditCaptor.getValue().getNewValue());
    }

    @Test
    void shouldNotCreateAuditLogWhenAssigneeIsUnchanged() {
        Task task = createTask();
        User assignee = createUser("Same", "Assignee");
        task.setAssignee(assignee);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(currentUserProvider.getCurrentUser()).thenReturn(createUser("Current", "User"));
        when(userRepository.findById(assignee.getId())).thenReturn(Optional.of(assignee));

        taskService.changeAssignee(task.getId(), new ChangeAssigneeRequest(assignee.getId()));

        verify(auditLogRepository, never()).save(any(AuditLog.class));
    }

    @Test
    void shouldThrowWhenNewAssigneeDoesNotExist() {
        Task task = createTask();
        UUID assigneeId = UUID.randomUUID();
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(currentUserProvider.getCurrentUser()).thenReturn(createUser("Current", "User"));
        when(userRepository.findById(assigneeId)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.changeAssignee(task.getId(), new ChangeAssigneeRequest(assigneeId))
        );
        verify(auditLogRepository, never()).save(any(AuditLog.class));
    }

    @Test
    void shouldDeleteTaskWhenCurrentUserIsCreator() {
        Task task = createTask();
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(currentUserProvider.getCurrentUser()).thenReturn(task.getCreatedBy());

        taskService.delete(task.getId());

        verify(taskRepository).delete(task);
        verify(membershipRepository, never()).existsByProjectIdAndUserIdAndRole(
                any(),
                any(),
                any()
        );
    }

    @Test
    void shouldDeleteTaskWhenCurrentUserIsAdministrator() {
        Task task = createTask();
        User administrator = createUser("System", "Administrator");
        administrator.getRoles().add(new Role("ADMIN"));
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(currentUserProvider.getCurrentUser()).thenReturn(administrator);

        taskService.delete(task.getId());

        verify(taskRepository).delete(task);
        verify(membershipRepository, never()).existsByProjectIdAndUserIdAndRole(
                any(),
                any(),
                any()
        );
    }

    @Test
    void shouldDeleteTaskWhenCurrentUserIsProjectManager() {
        Task task = createTask();
        User projectManager = createUser("Project", "Manager");
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(currentUserProvider.getCurrentUser()).thenReturn(projectManager);
        when(membershipRepository.existsByProjectIdAndUserIdAndRole(
                task.getProject().getId(),
                projectManager.getId(),
                ProjectRole.PROJECT_MANAGER
        )).thenReturn(true);

        taskService.delete(task.getId());

        verify(taskRepository).delete(task);
    }

    @Test
    void shouldRejectTaskDeletionWhenUserHasNoPermission() {
        Task task = createTask();
        User currentUser = createUser("Unauthorized", "User");
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(currentUserProvider.getCurrentUser()).thenReturn(currentUser);
        when(membershipRepository.existsByProjectIdAndUserIdAndRole(
                task.getProject().getId(),
                currentUser.getId(),
                ProjectRole.PROJECT_MANAGER
        )).thenReturn(false);

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> taskService.delete(task.getId())
        );

        assertEquals("You cannot delete this task", exception.getMessage());
        verify(taskRepository, never()).delete(any(Task.class));
    }

    private Task createTask() {
        Task task = new Task();
        task.setId(UUID.randomUUID());
        task.setTitle("Test title");
        task.setDescription("Test description");
        task.setPriority(TaskPriority.HIGH);
        task.setDueDate(LocalDate.of(2026, 6, 20));
        task.setProject(createProject(UUID.randomUUID()));
        task.setStatus(createStatus("TODO", "To do"));
        task.setCreatedBy(createUser("Test", "Creator"));
        return task;
    }

    private Project createProject(UUID projectId) {
        Project project = new Project();
        project.setId(projectId);
        return project;
    }

    private TaskStatus createStatus(String code, String name) {
        TaskStatus status = new TaskStatus();
        status.setCode(code);
        status.setName(name);
        return status;
    }

    private User createUser(String firstName, String lastName) {
        try {
            var constructor = User.class.getDeclaredConstructor();
            constructor.setAccessible(true);

            User user = constructor.newInstance();
            user.setId(UUID.randomUUID());
            user.setFirstName(firstName);
            user.setLastName(lastName);
            return user;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not create test user", exception);
        }
    }
}
