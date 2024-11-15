package com.code_galacticos.taskservice.model.entity;

import com.code_galacticos.taskservice.model.entity.convertor.TaskPriorityConverter;
import com.code_galacticos.taskservice.model.entity.convertor.TaskStatusConverter;
import com.code_galacticos.taskservice.model.enums.TaskPriority;
import com.code_galacticos.taskservice.model.enums.TaskStatus;
import lombok.Data;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "task_table")
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "task_uuid")
    private UUID id;

    @Column(name = "task_name", nullable = false)
    private String name;

    @Column(name = "task_description")
    private String description;

    @Convert(converter = TaskStatusConverter.class)
    @Column(name = "task_status")
    private TaskStatus status;

    @Convert(converter = TaskPriorityConverter.class)
    @Column(name = "priority")
    private TaskPriority priority;

    private LocalDateTime deadline;

    @ManyToOne
    @JoinColumn(name = "assignation_uuid")
    private UserEntity assignee;

    @ManyToOne
    @JoinColumn(name = "reporter_uuid")
    private UserEntity reporter;

    @ManyToOne
    @JoinColumn(name = "project_uuid")
    private ProjectEntity project;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
