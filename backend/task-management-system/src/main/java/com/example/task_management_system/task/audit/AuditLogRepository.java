package com.example.task_management_system.task.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    @Query("""
                select al
                from AuditLog al
                join fetch al.performedBy
                where al.task.id = :taskId
                order by al.timestamp desc
            """)
    List<AuditLog> findAllByTaskIdWithUser(UUID taskId);
}
