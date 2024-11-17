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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectUserConnectionService {
    private final UserProjectConnectionRepository userProjectConnectionRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final EmailNotificationSender emailNotificationSender;

    /**
     * Creates a connection between a user and a project with specified role
     *
     * @param userEmail email of the user to connect
     * @param projectId UUID of the project
     * @param userRole role to assign to the user in the project
     * @return Created UserProjectConnection
     * @throws UserNotFoundException if user not found
     * @throws ProjectNotFoundException if project not found
     * @throws UserProjectConnectionException if connection already exists
     */
    public UserProjectConnection createProjectUserConnection(String userEmail, UUID projectId, UserRole userRole) {
        // Get user by email
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + userEmail));

        // Get project by ID
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId));

        // Check if connection already exists
        List<UserProjectConnection> existingConnections = userProjectConnectionRepository.findAllByProjectId(projectId);
        UserProjectConnection existingConnection = existingConnections.stream()
                .filter(connection -> connection.getUser().getEmail().equals(userEmail))
                .findFirst()
                .orElse(null);

        if (existingConnection != null) {
            // Update existing connection with new role
            existingConnection.setRole(userRole);
            return userProjectConnectionRepository.save(existingConnection);
        } else {
            // Create new connection
            UserProjectConnection connection = new UserProjectConnection();
            connection.setId(UUID.randomUUID());
            connection.setUser(user);
            connection.setProject(project);
            connection.setRole(userRole);
            UserProjectConnection savedConnection = userProjectConnectionRepository.save(connection);
            EmailNotificationMessage emailNotificationMessage = EmailNotificationMessage.builder()
                    .to("olexandr.wowk@gmail.com")
                    .subject("You have been added to new project: " + project.getName())
                    .text("this is the body of the mail")
                    .build();
            emailNotificationSender.sendEmailNotification(emailNotificationMessage);
            return savedConnection;
        }
    }

    /**
     * Gets all projects for a specific user
     *
     * @param userId UUID of the user
     * @return List of ProjectEntity objects
     * @throws UserNotFoundException if user not found
     */
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
     * Gets user role in a specific project
     *
     * @param userId UUID of the user
     * @param projectId UUID of the project
     * @return UserRole of the user in the project
     * @throws UserProjectConnectionException if connection not found
     */
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
     * Gets all users in a project
     *
     * @param projectId UUID of the project
     * @return List of UserProjectConnection objects
     * @throws ProjectNotFoundException if project not found
     */
    public List<UserProjectConnection> getAllUsersInProject(UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException("Project not found with id: " + projectId);
        }

        return userProjectConnectionRepository.findAllByProjectId(projectId);
    }
}