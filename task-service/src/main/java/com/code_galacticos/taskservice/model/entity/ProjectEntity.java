package com.code_galacticos.taskservice.model.entity;

import com.code_galacticos.taskservice.model.entity.convertor.ProjectStatusConverter;
import com.code_galacticos.taskservice.model.enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;


@Data
@Entity
@Table(name = "project_table")
public class ProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "project_uuid")
    private UUID id;

    @Column(name = "project_name", nullable = false)
    private String name;

    @Convert(converter = ProjectStatusConverter.class)
    @Column(name = "project_status")
    private ProjectStatus status;


    @Column(name = "project_description")
    private String description;

}
