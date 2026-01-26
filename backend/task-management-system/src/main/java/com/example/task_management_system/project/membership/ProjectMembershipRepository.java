package com.example.task_management_system.project.membership;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectMembershipRepository extends JpaRepository<ProjectMembership, UUID> {

    boolean existsByProjectIdAndUserId(UUID projectId, UUID userId);

    Optional<ProjectMembership> findByProjectIdAndUserId(UUID projectId, UUID userId);

    List<ProjectMembership> findAllByProjectId(UUID projectId);

    void deleteByProjectIdAndUserId(UUID projectId, UUID userId);
}
