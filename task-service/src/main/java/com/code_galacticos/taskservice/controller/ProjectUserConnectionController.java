package com.code_galacticos.taskservice.controller;

import com.code_galacticos.taskservice.exception.ProjectNotFoundException;
import com.code_galacticos.taskservice.exception.UserNotFoundException;
import com.code_galacticos.taskservice.exception.UserProjectConnectionException;
import com.code_galacticos.taskservice.model.entity.ProjectEntity;
import com.code_galacticos.taskservice.model.entity.UserProjectConnection;
import com.code_galacticos.taskservice.model.enums.UserRole;
import com.code_galacticos.taskservice.service.ProjectUserConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/project-connections")
@RequiredArgsConstructor
public class ProjectUserConnectionController {
    private final ProjectUserConnectionService projectUserConnectionService;

    /**
     * Create or update user-project connection
     *
     * @param projectId Project UUID
     * @param userEmail Email of the user
     * @param userRole Role to assign
     * @return Created or updated connection
     */
    @PostMapping("/{projectId}")
    public ResponseEntity<UserProjectConnection> createUserProjectConnection(
            @PathVariable UUID projectId,
            @RequestParam String userEmail,
            @RequestParam UserRole userRole) {
        UserProjectConnection connection = projectUserConnectionService
                .createProjectUserConnection(userEmail, projectId, userRole);
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
            @PathVariable UUID projectId,
            @PathVariable UUID userId) {
        UserRole role = projectUserConnectionService.getUserRoleInProject(userId, projectId);
        return ResponseEntity.ok(role);
    }

    /**
     * Get all users in a project
     *
     * @param projectId Project UUID
     * @return List of user connections
     */
    @GetMapping("/{projectId}/")
    public ResponseEntity<List<UserProjectConnection>> getProjectUsers(
            @PathVariable UUID projectId) {
        List<UserProjectConnection> users = projectUserConnectionService.getAllUsersInProject(projectId);
        return ResponseEntity.ok(users);
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
     * Exception handler for UserProjectConnectionException
     */
    @ExceptionHandler(UserProjectConnectionException.class)
    public ResponseEntity<ErrorResponse> handleUserProjectConnectionException(UserProjectConnectionException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
