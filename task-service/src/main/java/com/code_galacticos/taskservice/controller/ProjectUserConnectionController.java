package com.code_galacticos.taskservice.controller;

import com.code_galacticos.taskservice.exception.ProjectNotFoundException;
import com.code_galacticos.taskservice.exception.UserNotFoundException;
import com.code_galacticos.taskservice.exception.UserProjectConnectionException;
import com.code_galacticos.taskservice.model.dto.project.ProjectUserConnectionDto;
import com.code_galacticos.taskservice.model.entity.ProjectEntity;
import com.code_galacticos.taskservice.model.entity.UserEntity;
import com.code_galacticos.taskservice.model.entity.UserProjectConnection;
import com.code_galacticos.taskservice.model.enums.UserRole;
import com.code_galacticos.taskservice.service.ProjectUserConnectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/project-connections")
@RequiredArgsConstructor
@Tag(name = "Project User Connections", description = "APIs for managing user connections to projects")
public class ProjectUserConnectionController {
    private final ProjectUserConnectionService projectUserConnectionService;

    /**
     * Create or update user-project connection
     *
     * @param projectId Project UUID
     * @param request User connection details
     * @return Created or updated connection
     */
    @PostMapping("/{projectId}")
    public ResponseEntity<UserProjectConnection> createUserProjectConnection(
            @Parameter(description = "Project UUID", required = true)
            @PathVariable UUID projectId,
            @Parameter(description = "User connection details", required = true)
            @Valid @RequestBody ProjectUserConnectionDto request) {
        UserProjectConnection connection = projectUserConnectionService
                .createProjectUserConnection(request, projectId);
        return new ResponseEntity<>(connection, HttpStatus.CREATED);
    }

    /**
     * Get all projects for a user
     *
     * @param userId User UUID
     * @return List of projects
     */
//    @GetMapping("/users/{userId}/projects")
//    public ResponseEntity<List<ProjectEntity>> getUserProjects(
//            @PathVariable UUID userId) {
//        List<ProjectEntity> projects = projectUserConnectionService.getAllProjectsForUser(userId);
//        return ResponseEntity.ok(projects);
//    }

    /**
     * Get user's role in a project
     *
     * @param projectId Project UUID
     * @param userId User UUID
     * @return User's role
     */
    @GetMapping("{projectId}/{userId}")
    public ResponseEntity<UserRole> getUserRoleInProject(
            @Parameter(description = "Project UUID", required = true)
            @PathVariable UUID projectId,
            @Parameter(description = "User UUID", required = true)
            @PathVariable UUID userId) {
        UserRole role = projectUserConnectionService.getUserRoleInProject(userId, projectId);
        return ResponseEntity.ok(role);
    }

    @Operation(
            summary = "Get project users",
            description = "Retrieves all users associated with a project"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = UserEntity.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found"
            )
    })
    @GetMapping("/{projectId}")
    public ResponseEntity<List<UserEntity>> getProjectUsers(
            @Parameter(description = "Project UUID", required = true)
            @PathVariable UUID projectId) {
        List<UserProjectConnection> connections = projectUserConnectionService.getAllUsersInProject(projectId);
        List<UserEntity> users = connections.stream()
                .map(UserProjectConnection::getUser)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Remove user from project",
            description = "Removes a user from a project"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "User successfully removed from project"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project or user not found"
            )
    })
    @DeleteMapping("/{projectId}/users/{userId}")
    public ResponseEntity<Void> deleteUserFromProject(
            @Parameter(description = "Project UUID", required = true)
            @PathVariable UUID projectId,
            @Parameter(description = "User's email address", required = true, example = "user@example.com")
            @PathVariable UUID userId) {
        projectUserConnectionService.deleteUserFromProject(projectId, userId);
        return ResponseEntity.noContent().build();
    }
}
