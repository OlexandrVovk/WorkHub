package com.code_galacticos.taskservice.service;

import com.code_galacticos.taskservice.exception.ProjectNotFoundException;
import com.code_galacticos.taskservice.exception.UserNotFoundException;
import com.code_galacticos.taskservice.model.entity.ProjectEntity;
import com.code_galacticos.taskservice.model.entity.UserEntity;
import com.code_galacticos.taskservice.model.entity.UserProjectConnection;
import com.code_galacticos.taskservice.model.enums.ProjectStatus;
import com.code_galacticos.taskservice.model.enums.UserRole;
import com.code_galacticos.taskservice.repository.ProjectRepository;
import com.code_galacticos.taskservice.repository.TaskRepository;
import com.code_galacticos.taskservice.repository.UserProjectConnectionRepository;
import com.code_galacticos.taskservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserProjectConnectionRepository userProjectConnectionRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService projectService;

    private UUID projectId;
    private UUID userId;
    private ProjectEntity projectEntity;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();
        userId = UUID.fromString("41c2be26-6c45-4ea9-995d-f7bc183ce7d7");

        projectEntity = new ProjectEntity();
        projectEntity.setId(projectId);
        projectEntity.setName("Test Project");
        projectEntity.setDescription("Test Description");
        projectEntity.setStatus(ProjectStatus.ACTIVE);

        userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail("test@example.com");
        userEntity.setFirstName("Test");
        userEntity.setLastName("User");
    }

    @Test
    void createProject_Success() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(projectRepository.save(any(ProjectEntity.class))).thenReturn(projectEntity);
        when(userProjectConnectionRepository.save(any(UserProjectConnection.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ProjectEntity result = projectService.createProject(projectEntity, userId);

        // Assert
        assertNotNull(result);
        assertEquals(projectId, result.getId());
        assertEquals("Test Project", result.getName());
        verify(userProjectConnectionRepository).save(argThat(connection ->
                connection.getUser().equals(userEntity) &&
                        connection.getProject().equals(projectEntity) &&
                        connection.getRole().equals(UserRole.OWNER)
        ));
    }

    @Test
    void createProject_UserNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () ->
                projectService.createProject(projectEntity, userId)
        );
        verify(projectRepository, never()).save(any());
        verify(userProjectConnectionRepository, never()).save(any());
    }

    @Test
    void getProjectById_Success() {
        // Arrange
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectEntity));

        // Act
        ProjectEntity result = projectService.getProjectById(projectId);

        // Assert
        assertNotNull(result);
        assertEquals(projectId, result.getId());
        assertEquals("Test Project", result.getName());
    }

    @Test
    void getProjectById_NotFound() {
        // Arrange
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProjectNotFoundException.class, () ->
                projectService.getProjectById(projectId)
        );
    }

    @Test
    void updateProject_Success() {
        // Arrange
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(projectRepository.save(any(ProjectEntity.class))).thenReturn(projectEntity);

        // Act
        ProjectEntity result = projectService.updateProject(projectEntity);

        // Assert
        assertNotNull(result);
        assertEquals(projectId, result.getId());
        assertEquals("Test Project", result.getName());
    }

    @Test
    void updateProject_NotFound() {
        // Arrange
        when(projectRepository.existsById(projectId)).thenReturn(false);

        // Act & Assert
        assertThrows(ProjectNotFoundException.class, () ->
                projectService.updateProject(projectEntity)
        );
        verify(projectRepository, never()).save(any());
    }

    @Test
    void deleteProject_Success() {
        // Arrange
        when(projectRepository.existsById(projectId)).thenReturn(true);

        // Act
        projectService.deleteProject(projectId);

        // Assert
        verify(taskRepository).deleteAllByProjectId(projectId);
        verify(userProjectConnectionRepository).deleteAllByProjectId(projectId);
        verify(projectRepository).deleteByProjectId(projectId);
    }

    @Test
    void deleteProject_NotFound() {
        // Arrange
        when(projectRepository.existsById(projectId)).thenReturn(false);

        // Act & Assert
        assertThrows(ProjectNotFoundException.class, () ->
                projectService.deleteProject(projectId)
        );
        verify(taskRepository, never()).deleteAllByProjectId(any());
        verify(userProjectConnectionRepository, never()).deleteAllByProjectId(any());
        verify(projectRepository, never()).deleteByProjectId(any());
    }
}