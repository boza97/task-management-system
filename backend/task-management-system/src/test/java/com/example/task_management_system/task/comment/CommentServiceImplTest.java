package com.example.task_management_system.task.comment;

import com.example.task_management_system.common.exception.ResourceNotFoundException;
import com.example.task_management_system.common.security.CurrentUserProvider;
import com.example.task_management_system.task.Task;
import com.example.task_management_system.task.TaskRepository;
import com.example.task_management_system.task.audit.AuditLogRepository;
import com.example.task_management_system.task.comment.dto.CommentCreateRequest;
import com.example.task_management_system.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.lang.reflect.Field;
import java.util.HashSet;
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
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        user = createUser(UUID.randomUUID());
        task = new Task();
        setField(task, "id", UUID.randomUUID());
    }

    @Test
    void shouldAddCommentSuccessfully() {
        UUID taskId = task.getId();
        CommentCreateRequest request = new CommentCreateRequest("Test comment");

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(commentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var response = commentService.addComment(taskId, request);

        assertThat(response).isNotNull();
        assertThat(response.content()).isEqualTo("Test comment");

        verify(commentRepository).save(any());
        verify(auditLogRepository).save(any());
    }

    @Test
    void shouldThrowWhenContentEmpty() {
        CommentCreateRequest request = new CommentCreateRequest("   ");

        assertThatThrownBy(() ->
                                   commentService.addComment(UUID.randomUUID(), request)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Comment content must not be empty");

        verify(commentRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenTaskNotFound() {
        UUID taskId = UUID.randomUUID();
        CommentCreateRequest request = new CommentCreateRequest("Test");

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                                   commentService.addComment(taskId, request)
        ).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldReturnComments() {
        UUID taskId = task.getId();

        Comment comment = new Comment();
        setField(comment, "id", UUID.randomUUID());
        comment.setContent("Hello");
        comment.setAuthor(user);
        comment.setTask(task);

        when(taskRepository.existsById(taskId)).thenReturn(true);
        when(commentRepository.findAllByTaskIdOrderByCreatedAtDesc(taskId))
                .thenReturn(List.of(comment));

        var result = commentService.getComments(taskId);

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldThrowWhenTaskNotExistsForGet() {
        when(taskRepository.existsById(any())).thenReturn(false);

        assertThatThrownBy(() ->
                                   commentService.getComments(UUID.randomUUID())
        ).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldDeleteCommentAsAuthor() throws Exception {
        UUID taskId = task.getId();
        UUID commentId = UUID.randomUUID();

        Comment comment = new Comment();
        setField(comment, "id", commentId);
        comment.setTask(task);
        comment.setAuthor(user);
        comment.setContent("test");

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(taskRepository.existsById(taskId)).thenReturn(true);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.deleteComment(taskId, commentId);

        verify(commentRepository).delete(comment);
        verify(auditLogRepository).save(any());
    }

    @Test
    void shouldThrowWhenNotAuthorOrAdmin() {
        UUID taskId = task.getId();
        UUID commentId = UUID.randomUUID();

        User otherUser = createUser(UUID.randomUUID());

        Comment comment = new Comment();
        setField(comment, "id", commentId);
        comment.setTask(task);
        comment.setAuthor(otherUser);

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(taskRepository.existsById(taskId)).thenReturn(true);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() ->
                                   commentService.deleteComment(taskId, commentId)
        ).isInstanceOf(AccessDeniedException.class)
                .hasMessage("Only comment author or admin can delete comment");

        verify(commentRepository, never()).delete(comment);
    }

    private User createUser(UUID id) {
        try {
            var constructor = User.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            User u = constructor.newInstance();
            setField(u, "id", id);
            setField(u, "firstName", "Test");
            setField(u, "lastName", "User");
            setField(u, "roles", new HashSet<>());

            return u;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
