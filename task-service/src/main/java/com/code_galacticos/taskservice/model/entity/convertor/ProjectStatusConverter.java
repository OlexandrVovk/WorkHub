package com.code_galacticos.taskservice.model.entity.convertor;

import com.code_galacticos.taskservice.model.enums.ProjectStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ProjectStatusConverter implements AttributeConverter<ProjectStatus, String> {
    @Override
    public String convertToDatabaseColumn(ProjectStatus status) {
        if (status == null) return null;
        return status.name();
    }

    @Override
    public ProjectStatus convertToEntityAttribute(String status) {
        if (status == null) return null;
        return ProjectStatus.valueOf(status);
    }
}

