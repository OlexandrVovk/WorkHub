package com.code_galacticos.taskservice.model.dto.project;

import com.code_galacticos.taskservice.model.enums.UserRole;
import com.google.firebase.database.annotations.NotNull;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class ProjectUserConnectionDto {
    @NotNull
    @Email
    private String userEmail;

    @NotNull
    private UserRole userRole;
}