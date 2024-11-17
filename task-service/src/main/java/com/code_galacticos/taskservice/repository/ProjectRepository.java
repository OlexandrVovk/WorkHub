package com.code_galacticos.taskservice.repository;

import com.code_galacticos.taskservice.model.entity.ProjectEntity;
import com.code_galacticos.taskservice.model.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, UUID> {
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ProjectEntity p WHERE p.id = :projectId")
    void deleteByProjectId(@Param("projectId") UUID projectId);

    Optional<ProjectEntity> findById(UUID id);
}