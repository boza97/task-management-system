package com.example.task_management_system.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {

    List<Task> findAllByProjectIdOrderByUpdatedAtDesc(UUID projectId);

    boolean existsByProjectIdAndAssigneeId(UUID projectId, UUID assigneeId);

}
