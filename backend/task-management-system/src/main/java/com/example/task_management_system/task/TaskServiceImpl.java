package com.example.task_management_system.task;

import com.example.task_management_system.common.exception.ResourceNotFoundException;
import com.example.task_management_system.common.security.CurrentUserProvider;
import com.example.task_management_system.project.Project;
import com.example.task_management_system.project.ProjectRepository;
import com.example.task_management_system.task.audit.ActionType;
import com.example.task_management_system.task.audit.AuditLog;
import com.example.task_management_system.task.audit.AuditLogRepository;
import com.example.task_management_system.task.dto.ChangeAssigneeRequest;
import com.example.task_management_system.task.dto.ChangeStatusRequest;
import com.example.task_management_system.task.dto.TaskCreateRequest;
import com.example.task_management_system.task.dto.TaskResponse;
import com.example.task_management_system.task.dto.TaskUpdateRequest;
import com.example.task_management_system.task.status.TaskStatus;
import com.example.task_management_system.task.status.TaskStatusRepository;
import com.example.task_management_system.user.User;
import com.example.task_management_system.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

    @Override
    public TaskResponse create(TaskCreateRequest request) {
        User currentUser = currentUserProvider.getCurrentUser();
        Project project = projectRepository.findById(request.projectId())
                                           .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

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
            task.setAssignee(assignee);
        }

        task = taskRepository.save(task);
        createAudit(task, currentUser, ActionType.TASK_CREATED, null, null);

        return mapToResponse(task);
    }

    @Override
    public TaskResponse getById(UUID taskId) {
        return mapToResponse(getTaskOrThrow(taskId));
    }

    @Override
    public List<TaskResponse> getByProject(UUID projectId) {
        return taskRepository.findAllByProjectId(projectId).stream()
                             .map(this::mapToResponse).toList();
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

        UUID oldId = task.getAssignee() == null ? null : task.getAssignee().getId();
        UUID newId = request.assigneeId();

        if (Objects.equals(oldId, newId)) {
            return mapToResponse(task);
        }

        User newAssignee = null;
        if (newId != null) {
            newAssignee = userRepository.findById(newId)
                                        .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
        }

        String from = Objects.toString(oldId, null);
        String to = Objects.toString(newId, null);

        createAudit(task, currentUser, ActionType.ASSIGNEE_CHANGED, from, to);
        task.setAssignee(newAssignee);

        return mapToResponse(task);
    }

    @Override
    public void delete(UUID taskId) {
        Task task = getTaskOrThrow(taskId);
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
                task.getAssignee() != null ? task.getAssignee().getId() : null,
                task.getAssignee() != null ?
                        task.getAssignee().getFirstName() + " " + task.getAssignee().getLastName() : null,
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
