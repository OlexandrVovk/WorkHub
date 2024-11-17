package com.code_galacticos.taskservice.repository;

import com.code_galacticos.taskservice.model.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {
    List<TaskEntity> findAllByProjectId(UUID projectId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM TaskEntity t WHERE t.project.id = :projectId")
    void deleteAllByProjectId(@Param("projectId") UUID projectId);
}