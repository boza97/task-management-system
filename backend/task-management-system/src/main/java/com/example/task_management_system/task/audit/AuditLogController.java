package com.example.task_management_system.task.audit;

import com.example.task_management_system.task.audit.dto.AuditLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks/{taskId}/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public List<AuditLogResponse> getTaskAuditLogs(@PathVariable UUID taskId) {
        return auditLogService.getTaskAuditLogs(taskId);
    }
}
