package com.example.task_management_system.task.status;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class TaskStatusInitializer {

    private final TaskStatusRepository repository;

    public TaskStatusInitializer(TaskStatusRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        createIfMissing("TODO", "To Do", 1);
        createIfMissing("IN_DEVELOPMENT", "In Development", 2);
        createIfMissing("READY_FOR_CODE_REVIEW", "Ready for Code Review", 3);
        createIfMissing("READY_FOR_QA", "Ready for QA", 4);
        createIfMissing("IN_QA", "In QA", 5);
        createIfMissing("DONE", "Done", 6);
    }


    private void createIfMissing(String code, String name, int order) {
        if (!repository.existsByCode(code)) {
            repository.save(new TaskStatus(code, name, order));
        }
    }
}
