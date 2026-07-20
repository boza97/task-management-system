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
import com.example.task_management_system.task.dto.TaskResponse;
import com.example.task_management_system.task.dto.TaskSearchCriteria;
import com.example.task_management_system.task.dto.TaskUpdateRequest;
import com.example.task_management_system.task.search.TaskSpecifications;
import com.example.task_management_system.task.status.TaskStatus;
import com.example.task_management_system.task.status.TaskStatusRepository;
import com.example.task_management_system.user.User;
import com.example.task_management_system.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final AuditLogRepository auditLogRepository;
    private final ProjectRepository projectRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;
    private final ProjectMembershipRepository membershipRepository;
    private final NotificationService notificationService;

    @Override
    public TaskResponse create(TaskCreateRequest request) {
        User currentUser = currentUserProvider.getCurrentUser();
        Project project = projectRepository.findById(request.projectId())
                                           .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (!project.isOwner(currentUser) &&
            !membershipRepository.existsByProjectIdAndUserId(project.getId(), currentUser.getId())) {
            throw new AccessDeniedException("Only project members can create tasks");
        }

        TaskStatus defaultStatus = taskStatusRepository.findByCode("TODO")
                                                       .orElseThrow(() -> new IllegalStateException(
                                                               "Default status TODO missing"));

        Task task = new Task();
        task.setProject(project);
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setPriority(request.priority());
        task.setDueDate(request.dueDate());
        task.setStatus(defaultStatus);
        task.setCreatedBy(currentUser);

        if (request.assigneeId() != null) {
            User assignee = userRepository.findById(request.assigneeId())
                                          .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
            if (!membershipRepository.existsByProjectIdAndUserId(project.getId(), assignee.getId())) {
                throw new IllegalArgumentException("Assignee must be a project member");
            }
            task.setAssignee(assignee);
        }

        task = taskRepository.save(task);
        createAudit(task, currentUser, ActionType.TASK_CREATED, null, null);

        if (task.getAssignee() != null) {
            notificationService.notifyTaskAssigned(task.getAssignee(), task, currentUser);
        }

        return mapToResponse(task);
    }

    @Override
    public TaskResponse getById(UUID taskId) {
        return mapToResponse(getTaskOrThrow(taskId));
    }

    @Override
    public List<TaskResponse> getByProject(UUID projectId) {
        return taskRepository.findAllByProjectIdOrderByUpdatedAtDesc(projectId).stream()
                             .map(this::mapToResponse).toList();
    }

    @Override
    public List<TaskResponse> search(UUID projectId, TaskSearchCriteria criteria) {
        if (noFilters(criteria)) {
            return getByProject(projectId);
        }

        Specification<Task> spec = Specification.where(
                TaskSpecifications.projectEquals(projectId)
        );

        if (hasText(criteria.search())) {
            spec = spec.and(TaskSpecifications.titleContains(criteria.search()));
        }

        if (criteria.priority() != null) {
            spec = spec.and(TaskSpecifications.priorityEquals(criteria.priority()));
        }

        if (criteria.assigneeId() != null) {
            spec = spec.and(TaskSpecifications.assigneeEquals(criteria.assigneeId()));
        }

        if (hasText(criteria.statusCode())) {
            spec = spec.and(TaskSpecifications.statusCodeEquals(criteria.statusCode()));
        }

        if (criteria.dueDateFrom() != null) {
            spec = spec.and(TaskSpecifications.dueDateFrom(criteria.dueDateFrom()));
        }

        if (criteria.dueDateTo() != null) {
            spec = spec.and(TaskSpecifications.dueDateTo(criteria.dueDateTo()));
        }

        return taskRepository.findAll(spec)
                             .stream()
                             .map(this::mapToResponse)
                             .toList();
    }

    @Override
    public TaskResponse update(UUID taskId, TaskUpdateRequest request) {
        Task task = getTaskOrThrow(taskId);
        User currentUser = currentUserProvider.getCurrentUser();

        if (!request.title().equals(task.getTitle())) {
            createAudit(task, currentUser, ActionType.TITLE_CHANGED, task.getTitle(), request.title());
            task.setTitle(request.title());
        }

        if (!Objects.equals(request.description(), task.getDescription())) {
            createAudit(task, currentUser, ActionType.DESCRIPTION_CHANGED,
                        task.getDescription(), request.description());
            task.setDescription(request.description());
        }

        if (!Objects.equals(request.dueDate(), task.getDueDate())) {
            createAudit(task, currentUser, ActionType.DUE_DATE_CHANGED,
                        String.valueOf(task.getDueDate()), String.valueOf(request.dueDate()));
            task.setDueDate(request.dueDate());
        }

        if (request.priority() != task.getPriority()) {
            createAudit(task, currentUser, ActionType.PRIORITY_CHANGED,
                        String.valueOf(task.getPriority()), String.valueOf(request.priority()));
            task.setPriority(request.priority());
        }

        return mapToResponse(task);
    }

    @Override
    public TaskResponse changeStatus(UUID taskId, ChangeStatusRequest request) {
        Task task = getTaskOrThrow(taskId);
        User currentUser = currentUserProvider.getCurrentUser();

        TaskStatus newStatus = taskStatusRepository.findByCode(request.statusCode())
                                                   .orElseThrow(
                                                           () -> new ResourceNotFoundException("Status not found"));

        String oldCode = task.getStatus().getCode();
        String newCode = newStatus.getCode();

        if (!oldCode.equals(newCode)) {
            createAudit(task, currentUser, ActionType.STATUS_CHANGED, oldCode, newCode);
            task.setStatus(newStatus);
        }

        return mapToResponse(task);
    }

    @Override
    public TaskResponse changeAssignee(UUID taskId, ChangeAssigneeRequest request) {
        Task task = getTaskOrThrow(taskId);
        User currentUser = currentUserProvider.getCurrentUser();

        User oldAssignee = task.getAssignee();
        UUID newAssigneeId = request.assigneeId();

        User newAssignee = null;
        if (newAssigneeId != null) {
            newAssignee = userRepository.findById(newAssigneeId)
                                        .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
            if (!membershipRepository.existsByProjectIdAndUserId(task.getProject().getId(), newAssignee.getId())) {
                throw new IllegalArgumentException("Assignee must be a project member");
            }
        }

        if (Objects.equals(
                oldAssignee == null ? null : oldAssignee.getId(),
                newAssignee == null ? null : newAssignee.getId()
        )) {
            return mapToResponse(task);
        }

        String from = oldAssignee == null
                ? "Unassigned"
                : oldAssignee.getFirstName() + " " + oldAssignee.getLastName();

        String to = newAssignee == null
                ? "Unassigned"
                : newAssignee.getFirstName() + " " + newAssignee.getLastName();

        createAudit(
                task,
                currentUser,
                ActionType.ASSIGNEE_CHANGED,
                from,
                to
        );

        task.setAssignee(newAssignee);

        if (newAssignee != null) {
            notificationService.notifyTaskAssigned(newAssignee, task, currentUser);
        }

        return mapToResponse(task);
    }

    @Override
    public void delete(UUID taskId) {
        Task task = getTaskOrThrow(taskId);
        User currentUser = currentUserProvider.getCurrentUser();

        requireDeletePermission(task, currentUser);
        taskRepository.delete(task);
    }

    private Task getTaskOrThrow(UUID taskId) {
        return taskRepository.findById(taskId)
                             .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    private void createAudit(Task task, User performedBy, ActionType type, String oldValue, String newValue) {
        AuditLog log = new AuditLog();
        log.setTask(task);
        log.setPerformedBy(performedBy);
        log.setActionType(type);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);

        auditLogRepository.save(log);
    }

    private void requireDeletePermission(Task task, User user) {
        if (user.hasRole("ADMIN")) {
            return;
        }

        if (task.getCreatedBy().getId().equals(user.getId())) {
            return;
        }

        boolean isProjectManager =
                membershipRepository.existsByProjectIdAndUserIdAndRole(
                        task.getProject().getId(),
                        user.getId(),
                        ProjectRole.PROJECT_MANAGER
                );

        if (isProjectManager) {
            return;
        }

        throw new AccessDeniedException("You cannot delete this task");

    }

    private boolean hasText(String s) {
        return s != null && !s.isBlank();
    }

    private boolean noFilters(TaskSearchCriteria criteria) {
        return criteria == null
               || (
                       !hasText(criteria.search())
                       && criteria.priority() == null
                       && criteria.assigneeId() == null
                       && !hasText(criteria.statusCode())
                       && criteria.dueDateFrom() == null
                       && criteria.dueDateTo() == null
               );
    }


    private TaskResponse mapToResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getDueDate(),
                task.getStatus().getCode(),
                task.getStatus().getName(),
                task.getProject().getId(),
                task.getCreatedBy().getId(),
                task.getCreatedBy().getFirstName() + " " + task.getCreatedBy().getLastName(),
                task.getAssignee() != null ? task.getAssignee().getId() : null,
                task.getAssignee() != null ?
                        task.getAssignee().getFirstName() + " " + task.getAssignee().getLastName() : null,
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
