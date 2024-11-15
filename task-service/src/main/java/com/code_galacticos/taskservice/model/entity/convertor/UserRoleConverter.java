package com.code_galacticos.taskservice.model.entity.convertor;

import com.code_galacticos.taskservice.model.enums.UserRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserRoleConverter implements AttributeConverter<UserRole, String> {
    @Override
    public String convertToDatabaseColumn(UserRole role) {
        if (role == null) return null;
        return role.name();
    }

    @Override
    public UserRole convertToEntityAttribute(String role) {
        if (role == null) return null;
        return UserRole.valueOf(role);
    }
}
