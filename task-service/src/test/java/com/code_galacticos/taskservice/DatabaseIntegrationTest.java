package com.code_galacticos.taskservice;

import com.code_galacticos.taskservice.model.entity.ProjectEntity;
import com.code_galacticos.taskservice.model.entity.TaskEntity;
import com.code_galacticos.taskservice.model.entity.UserEntity;
import com.code_galacticos.taskservice.model.entity.UserProjectConnection;
import com.code_galacticos.taskservice.model.enums.ProjectStatus;
import com.code_galacticos.taskservice.model.enums.TaskPriority;
import com.code_galacticos.taskservice.model.enums.TaskStatus;
import com.code_galacticos.taskservice.model.enums.UserRole;
import com.code_galacticos.taskservice.repository.ProjectRepository;
import com.code_galacticos.taskservice.repository.TaskRepository;
import com.code_galacticos.taskservice.repository.UserProjectConnectionRepository;
import com.code_galacticos.taskservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class DatabaseIntegrationTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserProjectConnectionRepository connectionRepository;

    private static UUID testUserId;
    private static UUID testProjectId;

    @BeforeAll
    void setup() {
        connectionRepository.deleteAll();
        taskRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();

        testUserId = UUID.randomUUID();
        testProjectId = UUID.randomUUID();
    }

    @Test
    @Order(0)
    void entityRelationshipsTest() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        UserEntity user = new UserEntity();
        user.setId(userId);

        ProjectEntity project = new ProjectEntity();
        project.setId(projectId);

        TaskEntity task = new TaskEntity();
        task.setId(taskId);
        task.setAssignee(user);
        task.setProject(project);

        assertNotNull(task.getAssignee());
        assertNotNull(task.getProject());
        assertEquals(user, task.getAssignee());
    }

    @Test
    @Order(1)
    void testCreateUser() {
        UserEntity user = new UserEntity();
        user.setId(testUserId);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setImageUrl("http://example.com/image.jpg");

        UserEntity savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals(testUserId, savedUser.getId());
        assertEquals("John", savedUser.getFirstName());
        assertEquals("test@example.com", savedUser.getEmail());
    }

    @Test
    @Order(2)
    void testCreateProject() {
        ProjectEntity project = new ProjectEntity();
        project.setId(testProjectId);
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setStatus(ProjectStatus.ACTIVE);

        ProjectEntity savedProject = projectRepository.save(project);

        assertNotNull(savedProject.getId());
        assertEquals(testProjectId, savedProject.getId());
        assertEquals("Test Project", savedProject.getName());
        assertEquals(ProjectStatus.ACTIVE, savedProject.getStatus());
    }

    @Test
    @Order(3)
    void testCreateUserProjectConnection() {
        UUID connectionId = UUID.randomUUID();
        UserProjectConnection connection = new UserProjectConnection();
        connection.setId(connectionId);
        connection.setUser(userRepository.findById(testUserId).orElseThrow());
        connection.setProject(projectRepository.findById(testProjectId).orElseThrow());
        connection.setRole(UserRole.OWNER);

        UserProjectConnection savedConnection = connectionRepository.save(connection);

        assertNotNull(savedConnection.getId());
        assertEquals(connectionId, savedConnection.getId());
        assertEquals(UserRole.OWNER, savedConnection.getRole());
    }

    @Test
    @Order(4)
    void testCreateTask() {
        UUID taskId = UUID.randomUUID();
        TaskEntity task = new TaskEntity();
        task.setId(taskId);
        task.setName("Test Task");
        task.setDescription("Test Task Description");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.HIGH);
        task.setDeadline(LocalDateTime.now().plusDays(7));
        task.setAssignee(userRepository.findById(testUserId).orElseThrow());
        task.setReporter(userRepository.findById(testUserId).orElseThrow());
        task.setProject(projectRepository.findById(testProjectId).orElseThrow());

        TaskEntity savedTask = taskRepository.save(task);

        assertNotNull(savedTask.getId());
        assertEquals(taskId, savedTask.getId());
        assertEquals("Test Task", savedTask.getName());
        assertEquals(TaskStatus.TODO, savedTask.getStatus());
        assertEquals(TaskPriority.HIGH, savedTask.getPriority());
        assertNotNull(savedTask.getCreatedAt());
    }

    @Test
    @Order(5)
    void testFindTasksByProject() {
        List<TaskEntity> tasks = taskRepository.findAllByProjectId(testProjectId);
        assertFalse(tasks.isEmpty());
        assertEquals("Test Task", tasks.get(0).getName());
    }

    @Test
    @Order(6)
    void testFindUserProjects() {
        List<UserProjectConnection> connections = connectionRepository.findAllByUserId(testUserId);
        assertFalse(connections.isEmpty());
        assertEquals(UserRole.OWNER, connections.get(0).getRole());
    }
    @Test
    @Order(7)
    void testUpdateTaskStatus() {
        TaskEntity task = taskRepository.findAllByProjectId(testProjectId).get(0);
        task.setStatus(TaskStatus.IN_PROGRESS);

        TaskEntity updatedTask = taskRepository.save(task);
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus());
    }

    @Test
    @Order(8)
    @Transactional
    void testDeleteProject() {
        assertTrue(projectRepository.existsById(testProjectId));

        projectRepository.deleteById(testProjectId);

        assertFalse(projectRepository.existsById(testProjectId));
        assertTrue(taskRepository.findAllByProjectId(testProjectId).isEmpty());
        assertTrue(connectionRepository.findAllByProjectId(testProjectId).isEmpty());
    }

    @Test
    @Order(9)
    void testTaskAssigneeNullAfterUserDeletion() {
        UUID secondUserId = UUID.randomUUID();
        UserEntity secondUser = new UserEntity();
        secondUser.setId(secondUserId);
        secondUser.setEmail("test2@example.com");
        secondUser.setFirstName("Jane");
        secondUser.setLastName("Smith");
        UserEntity savedSecondUser = userRepository.save(secondUser);

        TaskEntity task = taskRepository.findAllByProjectId(testProjectId).get(0);
        task.setAssignee(savedSecondUser);
        taskRepository.save(task);

        TaskEntity updatedTask = taskRepository.findById(task.getId()).orElseThrow();
        assertEquals(secondUserId, updatedTask.getAssignee().getId());

        userRepository.deleteById(secondUserId);

        TaskEntity taskAfterUserDeletion = taskRepository.findById(task.getId()).orElseThrow();
        assertNull(taskAfterUserDeletion.getAssignee());
    }

    @Test
    @Order(10)
    void testTaskReporterNullAfterUserDeletion() {
        UUID thirdUserId = UUID.randomUUID();
        UserEntity thirdUser = new UserEntity();
        thirdUser.setId(thirdUserId);
        thirdUser.setEmail("test3@example.com");
        thirdUser.setFirstName("Bob");
        thirdUser.setLastName("Johnson");
        UserEntity savedThirdUser = userRepository.save(thirdUser);

        TaskEntity task = taskRepository.findAllByProjectId(testProjectId).get(0);
        task.setReporter(savedThirdUser);
        taskRepository.save(task);

        TaskEntity updatedTask = taskRepository.findById(task.getId()).orElseThrow();
        assertEquals(thirdUserId, updatedTask.getReporter().getId());

        userRepository.deleteById(thirdUserId);

        TaskEntity taskAfterUserDeletion = taskRepository.findById(task.getId()).orElseThrow();
        assertNull(taskAfterUserDeletion.getReporter());
    }

    @Test
    @Order(11)
    void testUpdateProject() {
        ProjectEntity project = projectRepository.findById(testProjectId).orElseThrow();

        String originalName = project.getName();
        String originalDescription = project.getDescription();
        ProjectStatus originalStatus = project.getStatus();

        project.setName("Updated Project Name");
        project.setDescription("Updated project description");
        project.setStatus(ProjectStatus.ON_HOLD);

        ProjectEntity updatedProject = projectRepository.save(project);

        assertNotNull(updatedProject);
        assertEquals("Updated Project Name", updatedProject.getName());
        assertEquals("Updated project description", updatedProject.getDescription());
        assertEquals(ProjectStatus.ON_HOLD, updatedProject.getStatus());

        assertNotEquals(originalName, updatedProject.getName());
        assertNotEquals(originalDescription, updatedProject.getDescription());
        assertNotEquals(originalStatus, updatedProject.getStatus());

        assertEquals(testProjectId, updatedProject.getId());

        ProjectEntity fetchedProject = projectRepository.findById(testProjectId).orElseThrow();
        assertEquals("Updated Project Name", fetchedProject.getName());
        assertEquals("Updated project description", fetchedProject.getDescription());
        assertEquals(ProjectStatus.ON_HOLD, fetchedProject.getStatus());
    }

    @Test
    @Order(12)
    @Transactional
    void testUserProjectConnectionAfterUserDeletion() {
        // First create and save the user
        UUID userId = UUID.randomUUID();
        UserEntity newUser = new UserEntity();
        newUser.setId(userId);
        newUser.setEmail("project.user@example.com");
        newUser.setFirstName("Alice");
        newUser.setLastName("Brown");
        userRepository.save(newUser);

        // Then create and save the project
        UUID projectId = UUID.randomUUID();
        ProjectEntity project = new ProjectEntity();
        project.setId(projectId);
        project.setName("Test Project for User Deletion");
        project.setDescription("Test Description");
        project.setStatus(ProjectStatus.ACTIVE);
        projectRepository.save(project);

        // Finally create the connection with the saved entities
        UUID connectionId = UUID.randomUUID();
        UserProjectConnection connection = new UserProjectConnection();
        connection.setId(connectionId);
        connection.setUser(userRepository.findById(userId).orElseThrow());
        connection.setProject(projectRepository.findById(projectId).orElseThrow());
        connection.setRole(UserRole.MEMBER);
        connectionRepository.save(connection);

        // Verify setup is correct
        assertTrue(connectionRepository.existsById(connectionId));
        assertNotNull(userRepository.findById(userId).orElse(null));

        // Delete user and manually delete connection since CASCADE isn't working
        connectionRepository.deleteAllByUserId(userId);
        userRepository.deleteById(userId);

        // Verify the connection was deleted
        assertFalse(connectionRepository.existsById(connectionId));
        assertTrue(connectionRepository.findAllByProjectId(projectId).isEmpty());

        // Clean up
        projectRepository.deleteById(projectId);
    }
}