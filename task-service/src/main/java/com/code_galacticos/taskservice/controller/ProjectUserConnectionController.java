package com.code_galacticos.taskservice.controller;

import com.code_galacticos.taskservice.exception.ProjectNotFoundException;
import com.code_galacticos.taskservice.exception.UserNotFoundException;
import com.code_galacticos.taskservice.exception.UserProjectConnectionException;
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

    @Operation(
            summary = "Create project user connection",
            description = "Creates or updates a connection between a user and a project with specified role"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Connection created successfully",
                    content = @Content(schema = @Schema(implementation = UserProjectConnection.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project or user not found"
            )
    })
    @PostMapping("/{projectId}")
    public ResponseEntity<UserProjectConnection> createUserProjectConnection(
            @Parameter(description = "Project UUID", required = true)
            @PathVariable UUID projectId,
            @Parameter(description = "User's email address", required = true, example = "user@example.com")
            @RequestParam String userEmail,
            @Parameter(description = "Role to assign to the user", required = true)
            @RequestParam UserRole userRole) {
        UserProjectConnection connection = projectUserConnectionService
                .createProjectUserConnection(userEmail, projectId, userRole);
        return new ResponseEntity<>(connection, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get user role in project",
            description = "Retrieves the role of a specific user in a project"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Role retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserRole.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project or user not found"
            )
    })
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
    @DeleteMapping("/{projectId}/users/{userEmail}")
    public ResponseEntity<Void> deleteUserFromProject(
            @Parameter(description = "Project UUID", required = true)
            @PathVariable UUID projectId,
            @Parameter(description = "User's email address", required = true, example = "user@example.com")
            @PathVariable String userEmail) {
        projectUserConnectionService.deleteUserFromProject(projectId, userEmail);
        return ResponseEntity.noContent().build();
    }
}