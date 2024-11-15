package com.code_galacticos.taskservice.model.dto.project;

import lombok.Data;

import java.util.UUID;

@Data
public class ProjectResponseDto {
    private UUID id;
    private String name;
}