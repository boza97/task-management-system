package com.example.task_management_system.task.status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "task_status")
@Getter
@Setter
@NoArgsConstructor
public class TaskStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private java.util.UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int displayOrder;

    public TaskStatus(String code, String name, int displayOrder) {
        this.code = code;
        this.name = name;
        this.displayOrder = displayOrder;
    }
}
