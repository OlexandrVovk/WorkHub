package com.code_galacticos.taskservice.model.entity.convertor;

import com.code_galacticos.taskservice.model.enums.TaskPriority;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TaskPriorityConverter implements AttributeConverter<TaskPriority, String> {
    @Override
    public String convertToDatabaseColumn(TaskPriority priority) {
        if (priority == null) return null;
        return priority.name();
    }

    @Override
    public TaskPriority convertToEntityAttribute(String priority) {
        if (priority == null) return null;
        return TaskPriority.valueOf(priority);
    }
}
