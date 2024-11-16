package com.code_galacticos.taskservice.service;

import com.code_galacticos.taskservice.model.entity.ProjectEntity;
import com.code_galacticos.taskservice.model.entity.TaskEntity;
import com.code_galacticos.taskservice.model.entity.UserEntity;
import com.code_galacticos.taskservice.model.enums.TaskPriority;
import com.code_galacticos.taskservice.model.enums.TaskStatus;
import com.code_galacticos.taskservice.repository.ProjectRepository;
import com.code_galacticos.taskservice.repository.TaskRepository;
import com.code_galacticos.taskservice.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private UUID projectId;
    private UUID taskId;
    private UUID userId;
    private TaskEntity taskEntity;
    private ProjectEntity projectEntity;
    private UserEntity userEntity;
    private LocalDateTime deadline;

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();
        taskId = UUID.fromString("2231b85e-9219-4183-8e66-2e77d332101e");
        userId = UUID.randomUUID();
        deadline = LocalDateTime.now().plusDays(7);

        projectEntity = new ProjectEntity();
        projectEntity.setId(projectId);
        projectEntity.setName("Test Project");

        userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail("test@example.com");
        userEntity.setFirstName("Test User");

        taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        taskEntity.setName("Test Task");
        taskEntity.setDescription("Test Description");
        taskEntity.setStatus(TaskStatus.TODO);
        taskEntity.setPriority(TaskPriority.MEDIUM);
        taskEntity.setDeadline(deadline);
        taskEntity.setProject(projectEntity);
        taskEntity.setAssignee(userEntity);
        taskEntity.setReporter(userEntity);
    }

    @Test
    void getAllTasks_Success() {
        // Arrange
        List<TaskEntity> expectedTasks = Arrays.asList(taskEntity);
        when(taskRepository.findAllByProjectId(projectId)).thenReturn(expectedTasks);

        // Act
        List<TaskEntity> result = taskService.getAllTasks(projectId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(taskEntity.getName(), result.get(0).getName());
        verify(taskRepository).findAllByProjectId(projectId);
    }

    @Test
    void getTaskById_Success() {
        // Arrange
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));

        // Act
        TaskEntity result = taskService.getTaskById(projectId, taskId);

        // Assert
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals("Test Task", result.getName());
    }

    @Test
    void getTaskById_TaskNotFound() {
        // Arrange
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                taskService.getTaskById(projectId, taskId)
        );
    }

    @Test
    void getTaskById_WrongProject() {
        // Arrange
        UUID wrongProjectId = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));

        // Act & Assert
        assertThrows(SecurityException.class, () ->
                taskService.getTaskById(wrongProjectId, taskId)
        );
    }

    @Test
    void createTask_Success() {
        // Arrange
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectEntity));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);

        // Act
        TaskEntity result = taskService.createTask(projectId, taskEntity);

        // Assert
        assertNotNull(result);
        //assertEquals(taskId, result.getId());
        assertEquals("Test Task", result.getName());
        assertEquals(projectEntity, result.getProject());
        verify(taskRepository).save(any(TaskEntity.class));
    }

    @Test
    void createTask_ProjectNotFound() {
        // Arrange
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                taskService.createTask(projectId, taskEntity)
        );
        verify(taskRepository, never()).save(any());
    }

    @Test
    void updateTask_Success() {
        // Arrange
        TaskEntity updatedTask = new TaskEntity();
        updatedTask.setName("Updated Task");
        updatedTask.setDescription("Updated Description");
        updatedTask.setDeadline(LocalDateTime.now().plusDays(14));

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);

        // Act
        TaskEntity result = taskService.updateTask(userId, projectId, taskId, updatedTask);

        // Assert
        assertNotNull(result);
        verify(taskRepository).save(argThat(task ->
                task.getName().equals(updatedTask.getName()) &&
                        task.getDescription().equals(updatedTask.getDescription()) &&
                        task.getDeadline().equals(updatedTask.getDeadline())
        ));
    }

    @Test
    void deleteTask_Success() {
        // Arrange
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));

        // Act
        taskService.deleteTask(projectId, taskId);

        // Assert
        verify(taskRepository).delete(taskEntity);
    }

    @Test
    void updateTaskPriority_Success() {
        // Arrange
        TaskEntity updateRequest = new TaskEntity();
        updateRequest.setPriority(TaskPriority.HIGH);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);

        // Act
        TaskEntity result = taskService.updateTaskPriority(userId, projectId, taskId, updateRequest);

        // Assert
        assertNotNull(result);
        verify(taskRepository).save(argThat(task ->
                task.getPriority().equals(TaskPriority.HIGH)
        ));
    }

    @Test
    void updateTaskStatus_Success() {
        // Arrange
        TaskEntity updateRequest = new TaskEntity();
        updateRequest.setStatus(TaskStatus.IN_PROGRESS);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);

        // Act
        TaskEntity result = taskService.updateTaskStatus(userId, projectId, taskId, updateRequest);

        // Assert
        assertNotNull(result);
        verify(taskRepository).save(argThat(task ->
                task.getStatus().equals(TaskStatus.IN_PROGRESS)
        ));
    }

    @Test
    void updateTaskAssignee_Success() {
        // Arrange
        UUID newAssigneeId = UUID.randomUUID();
        UserEntity newAssignee = new UserEntity();
        newAssignee.setId(newAssigneeId);

        TaskEntity updateRequest = new TaskEntity();
        updateRequest.setAssignee(newAssignee);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(userRepository.findById(newAssigneeId)).thenReturn(Optional.of(newAssignee));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);

        // Act
        TaskEntity result = taskService.updateTaskAssignee(userId, projectId, taskId, updateRequest);

        // Assert
        assertNotNull(result);
        verify(taskRepository).save(argThat(task ->
                task.getAssignee().equals(newAssignee)
        ));
    }

    @Test
    void updateTaskAssignee_AssigneeNotFound() {
        // Arrange
        UUID newAssigneeId = UUID.randomUUID();
        UserEntity newAssignee = new UserEntity();
        newAssignee.setId(newAssigneeId);

        TaskEntity updateRequest = new TaskEntity();
        updateRequest.setAssignee(newAssignee);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(userRepository.findById(newAssigneeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                taskService.updateTaskAssignee(userId, projectId, taskId, updateRequest)
        );
        verify(taskRepository, never()).save(any());
    }

    @Test
    void updateTaskAssignee_RemoveAssignee() {
        // Arrange
        TaskEntity updateRequest = new TaskEntity();
        updateRequest.setAssignee(null);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);

        // Act
        TaskEntity result = taskService.updateTaskAssignee(userId, projectId, taskId, updateRequest);

        // Assert
        assertNotNull(result);
        verify(taskRepository).save(argThat(task ->
                task.getAssignee() == null
        ));
    }
}
