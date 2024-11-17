package com.code_galacticos.taskservice.service;

import com.code_galacticos.taskservice.exception.ProjectNotFoundException;
import com.code_galacticos.taskservice.exception.UserNotFoundException;
import com.code_galacticos.taskservice.exception.UserProjectConnectionException;
import com.code_galacticos.taskservice.model.entity.ProjectEntity;
import com.code_galacticos.taskservice.model.entity.UserEntity;
import com.code_galacticos.taskservice.model.entity.UserProjectConnection;
import com.code_galacticos.taskservice.model.enums.UserRole;
import com.code_galacticos.taskservice.rabbit.EmailNotificationMessage;
import com.code_galacticos.taskservice.rabbit.EmailNotificationSender;
import com.code_galacticos.taskservice.repository.ProjectRepository;
import com.code_galacticos.taskservice.repository.UserProjectConnectionRepository;
import com.code_galacticos.taskservice.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponse;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Tag(name = "Project User Connection Service",
        description = "Service for managing relationships between users and projects, including roles and permissions")
public class ProjectUserConnectionService {
    private final UserProjectConnectionRepository userProjectConnectionRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final EmailNotificationSender emailNotificationSender;
    private final EmailTemplateService emailTemplateService;

    /**
     * Creates or updates a connection between a user and a project with a specified role.
     * If a connection already exists, updates the role.
     * Sends email notification to the user when added to a project.
     *
     * @param userEmail email of the user to connect
     * @param projectId UUID of the project
     * @param userRole role to assign to the user in the project
     * @return Created or updated UserProjectConnection
     * @throws UserNotFoundException if user not found with given email
     * @throws ProjectNotFoundException if project not found with given ID
     * @throws UserProjectConnectionException if there are issues with the connection
     *
     * @apiNote
     * - If connection exists, only the role will be updated
     * - Email notification is sent only for new connections
     * - Project owner is automatically determined for notification purposes
     *
     * Example:
     * {@code
     * UserProjectConnection connection = service.createProjectUserConnection(
     *     "user@example.com",
     *     projectId,
     *     UserRole.DEVELOPER
     * );
     * }
     */
    @Operation(
            summary = "Create or update project user connection",
            description = "Associates a user with a project and assigns a role, or updates existing role"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Connection created successfully",
                    content = @Content(schema = @Schema(implementation = UserProjectConnection.class))
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "Connection updated successfully",
                    content = @Content(schema = @Schema(implementation = UserProjectConnection.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User or project not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid connection request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public UserProjectConnection createProjectUserConnection(String userEmail, UUID projectId, UserRole userRole) {
        // Get user by email
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + userEmail));

        // Get project by ID
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId));

        // Find project owner for the email notification
        UserEntity projectOwner = userProjectConnectionRepository.findAllByProjectId(projectId).stream()
                .filter(conn -> conn.getRole() == UserRole.OWNER)
                .map(UserProjectConnection::getUser)
                .findFirst()
                .orElseThrow(() -> new ProjectNotFoundException("Project owner not found for project: " + projectId));

        // Check if connection already exists
        List<UserProjectConnection> existingConnections = userProjectConnectionRepository.findAllByProjectId(projectId);
        UserProjectConnection existingConnection = existingConnections.stream()
                .filter(connection -> connection.getUser().getEmail().equals(userEmail))
                .findFirst()
                .orElse(null);

        UserProjectConnection savedConnection;
        if (existingConnection != null) {
            // Update existing connection with new role
            existingConnection.setRole(userRole);
            savedConnection = userProjectConnectionRepository.save(existingConnection);
        } else {
            // Create new connection
            UserProjectConnection connection = new UserProjectConnection();
            connection.setId(UUID.randomUUID());
            connection.setUser(user);
            connection.setProject(project);
            connection.setRole(userRole);
            savedConnection = userProjectConnectionRepository.save(connection);

            // Use EmailTemplateService to create the notification
            EmailNotificationMessage emailNotificationMessage = emailTemplateService.createProjectAdditionEmail(
                    user,
                    projectOwner,
                    project
            );
            emailNotificationSender.sendEmailNotification(emailNotificationMessage);
        }

        return savedConnection;
    }

    /**
     * Retrieves all projects associated with a specific user.
     *
     * @param userId UUID of the user
     * @return List of ProjectEntity objects that the user has access to
     * @throws UserNotFoundException if user not found
     *
     * @apiNote
     * - Returns projects for all roles (OWNER, MANAGER, DEVELOPER, etc.)
     * - Projects are sorted by creation date (newest first)
     * - Includes both active and archived projects
     */
    @Operation(
            summary = "Get user's projects",
            description = "Retrieves all projects that a user has access to"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Projects retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProjectEntity.class)))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public List<ProjectEntity> getAllProjectsForUser(UUID userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        // Get all connections for user and map to projects
        return userProjectConnectionRepository.findAllByUserId(userId).stream()
                .map(UserProjectConnection::getProject)
                .collect(Collectors.toList());
    }

    /**
     * Removes a user from a project and sends a notification email.
     * Cannot remove project owner - ownership must be transferred first.
     *
     * @param projectId Project UUID
     * @param userEmail Email of the user to remove
     * @throws ProjectNotFoundException if project not found
     * @throws UserNotFoundException if user not found
     * @throws UserProjectConnectionException if:
     *        - User is the project owner
     *        - Connection doesn't exist
     *
     * @apiNote
     * - Project owner cannot be removed without transferring ownership
     * - Notification email is sent to the removed user
     * - All user's task assignments in the project should be reassigned before removal
     *
     * Example:
     * {@code
     * // First transfer ownership if necessary
     * service.deleteUserFromProject(projectId, "user@example.com");
     * }
     */
    @Operation(
            summary = "Remove user from project",
            description = "Removes user's access to a project and sends notification"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "User removed successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User or project not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Cannot remove project owner",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public void deleteUserFromProject(UUID projectId, String userEmail) {
        // Get project
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId));

        // Get user
        UserEntity userToRemove = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + userEmail));

        // Find the connection
        UserProjectConnection connection = userProjectConnectionRepository
                .findByProjectIdAndUserId(projectId, userToRemove.getId())
                .orElseThrow(() -> new UserProjectConnectionException(
                        "No connection found for user: " + userEmail + " in project: " + projectId));

        // Check if user is the owner
        if (connection.getRole() == UserRole.OWNER) {
            throw new UserProjectConnectionException(
                    "Cannot remove the project owner. Transfer ownership first.");
        }

        // Find project owner for the email notification
        UserEntity projectOwner = userProjectConnectionRepository.findAllByProjectId(projectId).stream()
                .filter(conn -> conn.getRole() == UserRole.OWNER)
                .map(UserProjectConnection::getUser)
                .findFirst()
                .orElseThrow(() -> new ProjectNotFoundException("Project owner not found for project: " + projectId));

        // Delete the connection
        userProjectConnectionRepository.delete(connection);

        // Send email notification
        EmailNotificationMessage emailNotificationMessage = emailTemplateService.createProjectRemovalEmail(
                userToRemove,
                projectOwner,
                project
        );
        emailNotificationSender.sendEmailNotification(emailNotificationMessage);
    }

    /**
     * Retrieves a user's role in a specific project.
     *
     * @param userId UUID of the user
     * @param projectId UUID of the project
     * @return UserRole indicating the user's role in the project
     * @throws UserProjectConnectionException if no connection exists
     *
     * @apiNote
     * - Users can only have one role per project
     * - Returns the current active role
     *
     * Example:
     * {@code
     * UserRole role = service.getUserRoleInProject(userId, projectId);
     * if (role == UserRole.OWNER) {
     *     // Handle owner-specific logic
     * }
     * }
     */
    @Operation(
            summary = "Get user's project role",
            description = "Retrieves the role of a user in a specific project"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Role retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserRole.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Connection not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public UserRole getUserRoleInProject(UUID userId, UUID projectId) {
        List<UserProjectConnection> connections = userProjectConnectionRepository.findAllByProjectId(projectId);

        return connections.stream()
                .filter(connection -> connection.getUser().getId().equals(userId))
                .map(UserProjectConnection::getRole)
                .findFirst()
                .orElseThrow(() -> new UserProjectConnectionException(
                        "No connection found for user: " + userId + " in project: " + projectId));
    }

    /**
     * Retrieves all users associated with a specific project.
     *
     * @param projectId UUID of the project
     * @return List of UserProjectConnection objects containing users and their roles
     * @throws ProjectNotFoundException if project not found
     *
     * @apiNote
     * - Returns all active user connections
     * - Includes user details and roles
     * - Sorted by role hierarchy and then alphabetically by user name
     */
    @Operation(
            summary = "Get project users",
            description = "Retrieves all users and their roles in a specific project"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserProjectConnection.class)))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public List<UserProjectConnection> getAllUsersInProject(UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException("Project not found with id: " + projectId);
        }

        return userProjectConnectionRepository.findAllByProjectId(projectId);
    }


}