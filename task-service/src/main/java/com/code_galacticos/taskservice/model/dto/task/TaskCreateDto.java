package com.code_galacticos.taskservice.model.dto.task;

import lombok.Data;

@Data
public class TaskCreateDto {
    private String name;
    private String description;
}
