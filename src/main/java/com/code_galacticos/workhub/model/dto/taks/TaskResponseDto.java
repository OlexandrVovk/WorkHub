package com.code_galacticos.workhub.model.dto.taks;

import lombok.Data;

import java.util.UUID;

@Data
public class TaskResponseDto {
    private UUID id;
    private String name;
}