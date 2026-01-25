package com.example.task_management_system.task.comment;

import com.example.task_management_system.task.comment.dto.CommentCreateRequest;
import com.example.task_management_system.task.comment.dto.CommentResponse;

import java.util.List;
import java.util.UUID;

public interface CommentService {

    CommentResponse addComment(UUID taskId, CommentCreateRequest request);

    List<CommentResponse> getComments(UUID taskId);

    void deleteComment(UUID taskId, UUID commentId);
}
