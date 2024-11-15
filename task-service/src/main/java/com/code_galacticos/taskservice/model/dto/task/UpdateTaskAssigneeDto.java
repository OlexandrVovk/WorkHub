package com.code_galacticos.taskservice.model.dto.task;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateTaskAssigneeDto {
    private UUID id;
    private String name;
    private String description;
}