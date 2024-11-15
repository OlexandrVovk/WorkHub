package com.code_galacticos.taskservice.model.entity;

import com.code_galacticos.taskservice.model.entity.convertor.UserRoleConverter;
import com.code_galacticos.taskservice.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
@Table(name = "user_proj_connection")
public class UserProjectConnection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_proj_con_uuid")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "project_uuid")
    private ProjectEntity project;

    @ManyToOne
    @JoinColumn(name = "user_uuid")
    private UserEntity user;

    @Convert(converter = UserRoleConverter.class)
    @Column(name = "user_role")
    private UserRole role;
}
