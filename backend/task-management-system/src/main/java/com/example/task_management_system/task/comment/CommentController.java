package com.example.task_management_system.task.comment;

import com.example.task_management_system.task.comment.dto.CommentCreateRequest;
import com.example.task_management_system.task.comment.dto.CommentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse addComment(@PathVariable UUID taskId,
                                      @Valid @RequestBody CommentCreateRequest request) {
        return commentService.addComment(taskId, request);
    }

    @GetMapping
    public List<CommentResponse> getComments(@PathVariable UUID taskId) {
        return commentService.getComments(taskId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable UUID taskId, @PathVariable UUID commentId) {
        commentService.deleteComment(taskId, commentId);
    }
}
