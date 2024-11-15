package com.code_galacticos.taskservice.repository;

import com.code_galacticos.taskservice.model.entity.UserProjectConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProjectConnectionRepository extends JpaRepository<UserProjectConnection, UUID> {
    List<UserProjectConnection> findAllByUserId(UUID userId);
    List<UserProjectConnection> findAllByProjectId(UUID projectId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM UserProjectConnection c WHERE c.project.id = :projectId")
    void deleteAllByProjectId(@Param("projectId") UUID projectId);
}
