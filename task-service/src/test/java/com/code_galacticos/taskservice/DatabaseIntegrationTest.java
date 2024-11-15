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
    }

    @Test
    @Order(0)
    void entityRelationshipsTest() {
        UserEntity user = new UserEntity();
        ProjectEntity project = new ProjectEntity();
        TaskEntity task = new TaskEntity();

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
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setImageUrl("http://example.com/image.jpg");

        UserEntity savedUser = userRepository.save(user);
        testUserId = savedUser.getId();

        assertNotNull(savedUser.getId());
        assertEquals("John", savedUser.getFirstName());
        assertEquals("test@example.com", savedUser.getEmail());
    }

    @Test
    @Order(2)
    void testCreateProject() {
        ProjectEntity project = new ProjectEntity();
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setStatus(ProjectStatus.ACTIVE);

        ProjectEntity savedProject = projectRepository.save(project);
        testProjectId = savedProject.getId();

        assertNotNull(savedProject.getId());
        assertEquals("Test Project", savedProject.getName());
        assertEquals(ProjectStatus.ACTIVE, savedProject.getStatus());
    }

    @Test
    @Order(3)
    void testCreateUserProjectConnection() {
        UserProjectConnection connection = new UserProjectConnection();
        connection.setUser(userRepository.findById(testUserId).orElseThrow());
        connection.setProject(projectRepository.findById(testProjectId).orElseThrow());
        connection.setRole(UserRole.OWNER);

        UserProjectConnection savedConnection = connectionRepository.save(connection);

        assertNotNull(savedConnection.getId());
        assertEquals(UserRole.OWNER, savedConnection.getRole());
    }

    @Test
    @Order(4)
    void testCreateTask() {
        TaskEntity task = new TaskEntity();
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

        // Delete dependencies first
        taskRepository.deleteAllByProjectId(testProjectId);
        connectionRepository.deleteAllByProjectId(testProjectId);

        // Then delete project
        projectRepository.deleteById(testProjectId);

        assertFalse(projectRepository.existsById(testProjectId));
        assertTrue(taskRepository.findAllByProjectId(testProjectId).isEmpty());
        assertTrue(connectionRepository.findAllByProjectId(testProjectId).isEmpty());
    }
    @Test
    @Order(9)
    void testTaskAssigneeNullAfterUserDeletion() {
        // Create new user
        UserEntity secondUser = new UserEntity();
        secondUser.setEmail("test2@example.com");
        secondUser.setFirstName("Jane");
        secondUser.setLastName("Smith");
        UserEntity savedSecondUser = userRepository.save(secondUser);

        // Update task assignee
        TaskEntity task = taskRepository.findAllByProjectId(testProjectId).get(0);
        task.setAssignee(savedSecondUser);
        taskRepository.save(task);

        // Verify assignee set
        TaskEntity updatedTask = taskRepository.findById(task.getId()).orElseThrow();
        assertEquals(savedSecondUser.getId(), updatedTask.getAssignee().getId());

        // Delete user
        userRepository.deleteById(savedSecondUser.getId());

        // Verify assignee is null
        TaskEntity taskAfterUserDeletion = taskRepository.findById(task.getId()).orElseThrow();
        assertNull(taskAfterUserDeletion.getAssignee());
    }

    @Test
    @Order(10)
    void testTaskReporterNullAfterUserDeletion() {
        // Create new user
        UserEntity thirdUser = new UserEntity();
        thirdUser.setEmail("test3@example.com");
        thirdUser.setFirstName("Bob");
        thirdUser.setLastName("Johnson");
        UserEntity savedThirdUser = userRepository.save(thirdUser);

        // Update task reporter
        TaskEntity task = taskRepository.findAllByProjectId(testProjectId).get(0);
        task.setReporter(savedThirdUser);
        taskRepository.save(task);

        // Verify reporter set
        TaskEntity updatedTask = taskRepository.findById(task.getId()).orElseThrow();
        assertEquals(savedThirdUser.getId(), updatedTask.getReporter().getId());

        // Delete user
        userRepository.deleteById(savedThirdUser.getId());

        // Verify reporter is null
        TaskEntity taskAfterUserDeletion = taskRepository.findById(task.getId()).orElseThrow();
        assertNull(taskAfterUserDeletion.getReporter());
    }
    @Test
    @Order(11)
    void testUpdateProject() {
        // Find existing project
        ProjectEntity project = projectRepository.findById(testProjectId).orElseThrow();

        // Store original values for comparison
        String originalName = project.getName();
        String originalDescription = project.getDescription();
        ProjectStatus originalStatus = project.getStatus();

        // Update project fields
        project.setName("Updated Project Name");
        project.setDescription("Updated project description");
        project.setStatus(ProjectStatus.ON_HOLD);

        // Save updated project
        ProjectEntity updatedProject = projectRepository.save(project);

        // Verify project was updated
        assertNotNull(updatedProject);
        assertEquals("Updated Project Name", updatedProject.getName());
        assertEquals("Updated project description", updatedProject.getDescription());
        assertEquals(ProjectStatus.ON_HOLD, updatedProject.getStatus());

        // Verify original values were different
        assertNotEquals(originalName, updatedProject.getName());
        assertNotEquals(originalDescription, updatedProject.getDescription());
        assertNotEquals(originalStatus, updatedProject.getStatus());

        // Verify ID remained the same
        assertEquals(testProjectId, updatedProject.getId());

        // Verify data persists after fetching again
        ProjectEntity fetchedProject = projectRepository.findById(testProjectId).orElseThrow();
        assertEquals("Updated Project Name", fetchedProject.getName());
        assertEquals("Updated project description", fetchedProject.getDescription());
        assertEquals(ProjectStatus.ON_HOLD, fetchedProject.getStatus());
    }
    @Test
    @Order(12)
    @Transactional
    void testUserProjectConnectionAfterUserDeletion() {
        // Create new user
        UserEntity newUser = new UserEntity();
        newUser.setEmail("project.user@example.com");
        newUser.setFirstName("Alice");
        newUser.setLastName("Brown");
        UserEntity savedUser = userRepository.save(newUser);

        // Create new project for this test
        ProjectEntity project = new ProjectEntity();
        project.setName("Test Project for User Deletion");
        project.setDescription("Test Description");
        project.setStatus(ProjectStatus.ACTIVE);
        ProjectEntity savedProject = projectRepository.save(project);

        // Add user to project
        UserProjectConnection connection = new UserProjectConnection();
        connection.setUser(savedUser);
        connection.setProject(savedProject);
        connection.setRole(UserRole.MEMBER);
        UserProjectConnection savedConnection = connectionRepository.save(connection);

        // Store IDs for verification
        UUID userId = savedUser.getId();
        UUID projectId = savedProject.getId();
        UUID connectionId = savedConnection.getId();

        // Delete user
        userRepository.deleteById(userId);

        // Verify connection was deleted
        assertFalse(connectionRepository.existsById(connectionId));
        assertTrue(connectionRepository.findAllByProjectId(projectId).isEmpty());

        // Clean up
        projectRepository.deleteById(projectId);
    }
}