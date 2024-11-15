package com.code_galacticos.taskservice.model.dto.project;

import lombok.Data;

import java.util.UUID;

@Data
public class ProjectUpdateDto {
    private UUID id;
    private String name;
    private String description;
}
