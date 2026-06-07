package com.example.task_management_system.task.audit;

import com.example.task_management_system.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogService auditLogService;

    @Test
    void shouldReturnTaskAuditLogs() {
        UUID taskId = UUID.randomUUID();
        User user = new User("Test", "User", "test@example.com", "password");
        user.setId(UUID.randomUUID());
        AuditLog auditLog = new AuditLog(
                UUID.randomUUID(),
                ActionType.STATUS_CHANGED,
                Instant.now(),
                "OPEN",
                "DONE",
                null,
                user
        );
        when(auditLogRepository.findAllByTaskIdWithUser(taskId))
                .thenReturn(List.of(auditLog));

        var result = auditLogService.getTaskAuditLogs(taskId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().actionType()).isEqualTo(ActionType.STATUS_CHANGED);
        assertThat(result.getFirst().performedByName()).isEqualTo("Test User");
    }
}
