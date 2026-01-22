package com.example.task_management_system.project.membership;

import com.example.task_management_system.project.Project;
import com.example.task_management_system.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectMembershipRepository extends JpaRepository<ProjectMembership, UUID> {

    boolean existsByProjectAndUser(Project project, User user);

    Optional<ProjectMembership> findByProjectAndUser(Project project, User user);
}
