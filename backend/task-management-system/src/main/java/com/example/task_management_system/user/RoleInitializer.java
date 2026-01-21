package com.example.task_management_system.user;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer {
    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void init() {
        createIfNotExists("ADMIN");
        createIfNotExists("USER");
    }

    private void createIfNotExists(String roleName) {
        this.roleRepository.findByName(roleName).orElseGet(() -> roleRepository.save(new Role(roleName)));

    }
}
