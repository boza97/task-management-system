package com.example.task_management_system.task.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskStatusServiceTest {

    @Mock
    private TaskStatusRepository taskStatusRepository;

    @InjectMocks
    private TaskStatusService taskStatusService;

    @Test
    void shouldReturnStatusesInRepositoryOrder() {
        TaskStatus open = new TaskStatus("OPEN", "Open", 1);
        TaskStatus done = new TaskStatus("DONE", "Done", 2);
        when(taskStatusRepository.findAllByOrderByDisplayOrderAsc())
                .thenReturn(List.of(open, done));

        var result = taskStatusService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(status -> status.code())
                .containsExactly("OPEN", "DONE");
    }
}
