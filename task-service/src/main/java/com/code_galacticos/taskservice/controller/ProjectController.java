package com.code_galacticos.taskservice.controller;

import com.code_galacticos.taskservice.annotation.CurrentUser;
import com.code_galacticos.taskservice.exception.ProjectNotFoundException;
import com.code_galacticos.taskservice.exception.UserNotFoundException;
import com.code_galacticos.taskservice.model.entity.ProjectEntity;
import com.code_galacticos.taskservice.model.entity.UserEntity;
import com.code_galacticos.taskservice.service.ProjectService;
import com.code_galacticos.taskservice.service.ProjectUserConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectUserConnectionService projectUserConnectionService;

    /**
     * Create a new project
     *
     * @param projectEntity Project details
     * @param creatorUserId UUID of user creating the project (from header)
     * @return Created project
     */
    @PostMapping
    public ResponseEntity<ProjectEntity> createProject(
            @CurrentUser UserEntity creatorUserId,
            @RequestBody ProjectEntity projectEntity) {
        ProjectEntity createdProject = projectService.createProject(projectEntity, creatorUserId);
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    /**
     * Get project by ID
     *
     * @param projectId Project UUID
     * @return Project if found
     */
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectEntity> getProject(@PathVariable UUID projectId) {
        ProjectEntity project = projectService.getProjectById(projectId);
        return ResponseEntity.ok(project);
    }

    @GetMapping
    public ResponseEntity<List<ProjectEntity>> getUserProjects(
            @CurrentUser UserEntity userEntity) {
        List<ProjectEntity> projects = projectUserConnectionService.getAllProjectsForUser(userEntity.getId());
        return ResponseEntity.ok(projects);
    }
    /**
     * Update existing project
     *
     * @param projectId     Project UUID
     * @param projectEntity Updated project details
     * @return Updated project
     */
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectEntity> updateProject(
            @PathVariable UUID projectId,
            @RequestBody ProjectEntity projectEntity) {
        // Ensure the path variable ID matches the entity ID
        projectEntity.setId(projectId);
        ProjectEntity updatedProject = projectService.updateProject(projectEntity);
        return ResponseEntity.ok(updatedProject);
    }

    /**
     * Delete project
     *
     * @param projectId Project UUID
     * @return No content on success
     */
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Exception handler for ProjectNotFoundException
     */
    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProjectNotFoundException(ProjectNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Exception handler for UserNotFoundException
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}

/**
 * Error response class for consistent error handling
 */
record ErrorResponse(int status, String message) {}