package com.example.task_management_system.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    Optional<Project> findByKey(String key);

    boolean existsByKey(String key);

    @Query("""
                select distinct p from Project p
                left join ProjectMembership pm on pm.project = p
                where p.owner.id = :userId or pm.user.id = :userId
            """)
    List<Project> findAllByUser(UUID userId);
}
