package com.example.task_management_system.task.status;

import com.example.task_management_system.task.status.dto.TaskStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;

    public List<TaskStatusResponse> findAll() {
        return taskStatusRepository.findAllByOrderByDisplayOrderAsc()
                                   .stream()
                                   .map(ts -> new TaskStatusResponse(
                                           ts.getCode(),
                                           ts.getName(),
                                           ts.getDisplayOrder()
                                   )).toList();
    }
}
