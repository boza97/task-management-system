package com.example.task_management_system.task.search;

import com.example.task_management_system.task.Task;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class TaskSpecifications {
    public static Specification<Task> projectEquals(UUID projectId) {
        return (root, query, cb) ->
                cb.equal(root.get("project").get("id"), projectId);
    }

    public static Specification<Task> titleContains(String search) {
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("title")),
                        "%" + search.toLowerCase() + "%"
                );
    }

    public static Specification<Task> priorityEquals(Enum<?> priority) {
        return (root, query, cb) ->
                cb.equal(root.get("priority"), priority);
    }

    public static Specification<Task> assigneeEquals(UUID assigneeId) {
        return (root, query, cb) ->
                cb.equal(root.get("assignee").get("id"), assigneeId);
    }

    public static Specification<Task> statusCodeEquals(String statusCode) {
        return (root, query, cb) -> {
            var join = root.join("status", JoinType.INNER);
            return cb.equal(join.get("code"), statusCode);
        };
    }

    public static Specification<Task> dueDateFrom(java.time.LocalDate from) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("dueDate"), from);
    }

    public static Specification<Task> dueDateTo(java.time.LocalDate to) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("dueDate"), to);
    }
}
