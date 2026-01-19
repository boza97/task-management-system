package com.example.task_management_system.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    @Query("""
            
                select u from User u
                left join fetch u.roles
                where u.email = :email
            """)
    Optional<User> findByEmailWithRoles(@Param("email") String email);

}