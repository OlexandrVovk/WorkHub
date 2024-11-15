package com.code_galacticos.workhub.model.dto.taks;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateTaskPriorityDto {
    private UUID id;
    private String name;
    private String description;
}