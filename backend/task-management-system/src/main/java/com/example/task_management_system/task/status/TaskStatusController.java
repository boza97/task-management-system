package com.example.task_management_system.task.status;

import com.example.task_management_system.task.status.dto.TaskStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/task-statuses")
@RequiredArgsConstructor
public class TaskStatusController {

    private final TaskStatusService taskStatusService;

    @GetMapping
    public List<TaskStatusResponse> getAll() {
        return taskStatusService.findAll();
    }
}
