package com.code_galacticos.taskservice.service;

import com.code_galacticos.taskservice.exception.ProjectNotFoundException;
import com.code_galacticos.taskservice.exception.UserNotFoundException;
import com.code_galacticos.taskservice.exception.UserProjectConnectionException;
import com.code_galacticos.taskservice.model.entity.ProjectEntity;
import com.code_galacticos.taskservice.model.entity.UserEntity;
import com.code_galacticos.taskservice.model.entity.UserProjectConnection;
import com.code_galacticos.taskservice.model.enums.UserRole;
import com.code_galacticos.taskservice.repository.ProjectRepository;
import com.code_galacticos.taskservice.repository.UserProjectConnectionRepository;
import com.code_galacticos.taskservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectUserConnectionServiceTest {

    @Mock
    private UserProjectConnectionRepository userProjectConnectionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectUserConnectionService projectUserConnectionService;

    private UUID userId;
    private UUID projectId;
    private UUID connectionId;
    private String userEmail;
    private UserEntity userEntity;
    private ProjectEntity projectEntity;
    private UserProjectConnection connection;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        connectionId = UUID.randomUUID();
        userEmail = "test@example.com";

        userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail(userEmail);
        userEntity.setFirstName("Test");
        userEntity.setLastName("User");

        projectEntity = new ProjectEntity();
        projectEntity.setId(projectId);
        projectEntity.setName("Test Project");

        connection = new UserProjectConnection();
        connection.setId(connectionId);
        connection.setUser(userEntity);
        connection.setProject(projectEntity);
        connection.setRole(UserRole.MEMBER);
    }

    @Test
    void createProjectUserConnection_NewConnection_Success() {
        // Arrange
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectEntity));
        when(userProjectConnectionRepository.findAllByProjectId(projectId))
                .thenReturn(Collections.emptyList());
        when(userProjectConnectionRepository.save(any(UserProjectConnection.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserProjectConnection result = projectUserConnectionService
                .createProjectUserConnection(userEmail, projectId, UserRole.MEMBER);

        // Assert
        assertNotNull(result);
        assertEquals(userEntity, result.getUser());
        assertEquals(projectEntity, result.getProject());
        assertEquals(UserRole.MEMBER, result.getRole());
        verify(userProjectConnectionRepository).save(any(UserProjectConnection.class));
    }

    @Test
    void createProjectUserConnection_UpdateExisting_Success() {
        // Arrange
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectEntity));
        when(userProjectConnectionRepository.findAllByProjectId(projectId))
                .thenReturn(Collections.singletonList(connection));
        when(userProjectConnectionRepository.save(any(UserProjectConnection.class)))
                .thenReturn(connection);

        // Act
        UserProjectConnection result = projectUserConnectionService
                .createProjectUserConnection(userEmail, projectId, UserRole.TEAM_MANAGER);

        // Assert
        assertNotNull(result);
        assertEquals(UserRole.TEAM_MANAGER, result.getRole());
        verify(userProjectConnectionRepository).save(any(UserProjectConnection.class));
    }

    @Test
    void createProjectUserConnection_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () ->
                projectUserConnectionService.createProjectUserConnection(userEmail, projectId, UserRole.MEMBER)
        );
        verify(projectRepository, never()).findById(any());
        verify(userProjectConnectionRepository, never()).save(any());
    }

    @Test
    void createProjectUserConnection_ProjectNotFound() {
        // Arrange
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(userEntity));
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProjectNotFoundException.class, () ->
                projectUserConnectionService.createProjectUserConnection(userEmail, projectId, UserRole.MEMBER)
        );
        verify(userProjectConnectionRepository, never()).save(any());
    }

    @Test
    void getAllProjectsForUser_Success() {
        // Arrange
        List<UserProjectConnection> connections = Arrays.asList(connection);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userProjectConnectionRepository.findAllByUserId(userId)).thenReturn(connections);

        // Act
        List<ProjectEntity> results = projectUserConnectionService.getAllProjectsForUser(userId);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(projectEntity, results.get(0));
    }

    @Test
    void getAllProjectsForUser_UserNotFound() {
        // Arrange
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () ->
                projectUserConnectionService.getAllProjectsForUser(userId)
        );
        verify(userProjectConnectionRepository, never()).findAllByUserId(any());
    }

    @Test
    void getUserRoleInProject_Success() {
        // Arrange
        List<UserProjectConnection> connections = Arrays.asList(connection);
        when(userProjectConnectionRepository.findAllByProjectId(projectId)).thenReturn(connections);

        // Act
        UserRole result = projectUserConnectionService.getUserRoleInProject(userId, projectId);

        // Assert
        assertEquals(UserRole.MEMBER, result);
    }

    @Test
    void getUserRoleInProject_ConnectionNotFound() {
        // Arrange
        when(userProjectConnectionRepository.findAllByProjectId(projectId))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(UserProjectConnectionException.class, () ->
                projectUserConnectionService.getUserRoleInProject(userId, projectId)
        );
    }

    @Test
    void getAllUsersInProject_Success() {
        // Arrange
        List<UserProjectConnection> connections = Arrays.asList(connection);
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(userProjectConnectionRepository.findAllByProjectId(projectId)).thenReturn(connections);

        // Act
        List<UserProjectConnection> results = projectUserConnectionService.getAllUsersInProject(projectId);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(connection, results.get(0));
    }

    @Test
    void getAllUsersInProject_ProjectNotFound() {
        // Arrange
        when(projectRepository.existsById(projectId)).thenReturn(false);

        // Act & Assert
        assertThrows(ProjectNotFoundException.class, () ->
                projectUserConnectionService.getAllUsersInProject(projectId)
        );
        verify(userProjectConnectionRepository, never()).findAllByProjectId(any());
    }
}