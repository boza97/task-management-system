package com.example.task_management_system.task.comment;

import com.example.task_management_system.common.exception.ResourceNotFoundException;
import com.example.task_management_system.common.security.CurrentUserProvider;
import com.example.task_management_system.task.Task;
import com.example.task_management_system.task.TaskRepository;
import com.example.task_management_system.task.audit.ActionType;
import com.example.task_management_system.task.audit.AuditLog;
import com.example.task_management_system.task.audit.AuditLogRepository;
import com.example.task_management_system.task.comment.dto.CommentCreateRequest;
import com.example.task_management_system.task.comment.dto.CommentResponse;
import com.example.task_management_system.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final AuditLogRepository auditLogRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public CommentResponse addComment(UUID taskId, CommentCreateRequest request) {
        if (request.content() == null || request.content().isBlank()) {
            throw new IllegalArgumentException("Comment content must not be empty");
        }

        User currentUser = currentUserProvider.getCurrentUser();

        Task task = taskRepository.findById(taskId)
                                  .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        Comment comment = new Comment();
        comment.setTask(task);
        comment.setAuthor(currentUser);
        comment.setContent(request.content().trim());

        comment = commentRepository.save(comment);

        AuditLog log = new AuditLog();
        log.setTask(task);
        log.setPerformedBy(currentUser);
        log.setActionType(ActionType.COMMENT_ADDED);
        log.setOldValue(null);
        log.setNewValue(comment.getContent());
        auditLogRepository.save(log);

        return mapToResponse(comment);
    }

    @Override
    public List<CommentResponse> getComments(UUID taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found");
        }

        return commentRepository.findAllByTaskIdOrderByCreatedAtDesc(taskId).stream()
                                .map(this::mapToResponse)
                                .toList();
    }

    @Override
    public void deleteComment(UUID taskId, UUID commentId) {
        User currentUser = currentUserProvider.getCurrentUser();

        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found");
        }

        Comment comment = commentRepository.findById(commentId)
                                           .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getTask().getId().equals(taskId)) {
            throw new IllegalArgumentException("Comment does not belong to this task");
        }

        boolean isAuthor = comment.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().stream().anyMatch(r -> r.getName().equals("ADMIN"));

        if (!isAuthor && !isAdmin) {
            throw new SecurityException("Only comment author or admin can delete comment");
        }

        AuditLog log = new AuditLog();
        log.setTask(comment.getTask());
        log.setPerformedBy(currentUser);
        log.setActionType(ActionType.COMMENT_DELETED);
        log.setOldValue(comment.getContent());
        log.setNewValue(null);
        auditLogRepository.save(log);

        commentRepository.delete(comment);
    }

    private CommentResponse mapToResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getAuthor().getId(),
                comment.getAuthor().getEmail(),
                comment.getCreatedAt()
        );
    }
}
