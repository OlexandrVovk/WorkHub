package com.code_galacticos.taskservice.controller;

import com.code_galacticos.taskservice.annotation.CurrentUser;
import com.code_galacticos.taskservice.model.entity.ProjectEntity;
import com.code_galacticos.taskservice.model.entity.UserEntity;
import com.code_galacticos.taskservice.service.ProjectService;
import com.code_galacticos.taskservice.service.ProjectUserConnectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Project Management", description = "APIs for managing projects")
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectUserConnectionService projectUserConnectionService;

    @Operation(
            summary = "Create a new project",
            description = "Creates a new project with the provided details and assigns the creator as owner"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Project created successfully",
                    content = @Content(schema = @Schema(implementation = ProjectEntity.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<ProjectEntity> createProject(
            @Parameter(description = "Current authenticated user")
            @CurrentUser UserEntity creatorUserId,
            @Parameter(description = "Project details", required = true)
            @RequestBody ProjectEntity projectEntity) {
        ProjectEntity createdProject = projectService.createProject(projectEntity, creatorUserId);
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get project by ID",
            description = "Retrieves project details by its UUID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Project found",
                    content = @Content(schema = @Schema(implementation = ProjectEntity.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectEntity> getProject(
            @Parameter(description = "Project UUID", required = true)
            @PathVariable UUID projectId) {
        ProjectEntity project = projectService.getProjectById(projectId);
        return ResponseEntity.ok(project);
    }

    @Operation(
            summary = "Get all user projects",
            description = "Retrieves all projects associated with the current user"
    )
    @GetMapping
    public ResponseEntity<List<ProjectEntity>> getUserProjects(
            @Parameter(description = "Current authenticated user")
            @CurrentUser UserEntity userEntity) {
        List<ProjectEntity> projects = projectUserConnectionService.getAllProjectsForUser(userEntity.getId());
        return ResponseEntity.ok(projects);
    }

    @Operation(
            summary = "Update project",
            description = "Updates an existing project with new details"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Project updated successfully",
                    content = @Content(schema = @Schema(implementation = ProjectEntity.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectEntity> updateProject(
            @Parameter(description = "Project UUID", required = true)
            @PathVariable UUID projectId,
            @Parameter(description = "Updated project details", required = true)
            @RequestBody ProjectEntity projectEntity) {
        projectEntity.setId(projectId);
        ProjectEntity updatedProject = projectService.updateProject(projectEntity);
        return ResponseEntity.ok(updatedProject);
    }

    @Operation(
            summary = "Delete project",
            description = "Deletes a project by its UUID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Project deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
            @Parameter(description = "Project UUID", required = true)
            @PathVariable UUID projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }
}