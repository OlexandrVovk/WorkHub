package com.code_galacticos.workhub.model.dto.project;

import lombok.Data;

import java.util.UUID;

@Data
public class ProjectUpdateDto {
    private UUID id;
    private String name;
    private String description;
}