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

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DatabaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserProjectConnectionRepository connectionRepository;

    // Test data
    private UserEntity testUser;
    private UserEntity secondaryUser;
    private ProjectEntity testProject;
    private TaskEntity testTask;
    private UserProjectConnection testConnection;

    @BeforeAll
    @Transactional
    void setupTestData() {
        // Clean up any existing data
        cleanupDatabase();

        // Create test users
        testUser = createUser("test@example.com", "John", "Doe");
        secondaryUser = createUser("secondary@example.com", "Jane", "Smith");

        // Create test project
        testProject = createProject("Test Project");

        // Create test connection
        testConnection = createUserProjectConnection(testUser, testProject, UserRole.OWNER);

        // Create test task
        testTask = createTask("Test Task", testProject, testUser);
    }

    @AfterAll
    @Transactional
    void cleanup() {
        try {
            // Delete in correct order to avoid foreign key constraints
            connectionRepository.deleteAll();
            taskRepository.deleteAll();
            projectRepository.deleteAll();
            userRepository.deleteAll();

            // Verify cleanup
            assertTrue(connectionRepository.findAll().isEmpty(), "Connections were not cleaned up");
            assertTrue(taskRepository.findAll().isEmpty(), "Tasks were not cleaned up");
            assertTrue(projectRepository.findAll().isEmpty(), "Projects were not cleaned up");
            assertTrue(userRepository.findAll().isEmpty(), "Users were not cleaned up");
        } catch (Exception e) {
            fail("Cleanup failed: " + e.getMessage());
        }
    }

    private void cleanupDatabase() {
        try {
            connectionRepository.deleteAll();
            taskRepository.deleteAll();
            projectRepository.deleteAll();
            userRepository.deleteAll();
        } catch (Exception e) {
            fail("Database cleanup failed: " + e.getMessage());
        }
    }

    private UserEntity createUser(String email, String firstName, String lastName) {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setImageUrl("http://example.com/image.jpg");
        return userRepository.save(user);
    }

    private ProjectEntity createProject(String name) {
        ProjectEntity project = new ProjectEntity();
        project.setName(name);
        project.setDescription("Test Description");
        project.setStatus(ProjectStatus.ACTIVE);
        return projectRepository.save(project);
    }

    private UserProjectConnection createUserProjectConnection(
            UserEntity user,
            ProjectEntity project,
            UserRole role) {
        UserProjectConnection connection = new UserProjectConnection();
        connection.setUser(user);
        connection.setProject(project);
        connection.setRole(role);
        return connectionRepository.save(connection);
    }

    private TaskEntity createTask(
            String name,
            ProjectEntity project,
            UserEntity assignee) {
        TaskEntity task = new TaskEntity();
        task.setName(name);
        task.setDescription("Test Task Description");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.HIGH);
        task.setDeadline(LocalDateTime.now().plusDays(7));
        task.setAssignee(assignee);
        task.setReporter(assignee);
        task.setProject(project);
        return taskRepository.save(task);
    }

    @Nested
    @DisplayName("Creation Tests")
    class CreationTests {
        @Test
        void testEntityCreation() {
            // Verify user creation
            assertNotNull(testUser.getId());
            assertEquals("John", testUser.getFirstName());

            // Verify project creation
            assertNotNull(testProject.getId());
            assertEquals("Test Project", testProject.getName());

            // Verify connection creation
            assertNotNull(testConnection.getId());
            assertEquals(UserRole.OWNER, testConnection.getRole());

            // Verify task creation
            assertNotNull(testTask.getId());
            assertEquals("Test Task", testTask.getName());
        }
    }

    @Nested
    @DisplayName("Relationship Tests")
    class RelationshipTests {
        @Test
        void testUserProjectRelationship() {
            List<UserProjectConnection> connections =
                    connectionRepository.findAllByUserId(testUser.getId());
            assertFalse(connections.isEmpty());
            assertEquals(testProject.getId(),
                    connections.get(0).getProject().getId());
        }

        @Test
        void testProjectTaskRelationship() {
            List<TaskEntity> tasks =
                    taskRepository.findAllByProjectId(testProject.getId());
            assertFalse(tasks.isEmpty());
            assertEquals(testTask.getId(), tasks.get(0).getId());
        }
    }

    @Nested
    @DisplayName("Deletion Tests")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class DeletionTests {

        @Test
        @Order(1)
        @Transactional
        void testUserDeletionCascade() {
            // Create temporary test data
            UserEntity tempUser = createUser(
                    "temp@example.com",
                    "Temp",
                    "User"
            );
            TaskEntity tempTask = createTask(
                    "Temp Task",
                    testProject,
                    tempUser
            );

            // Delete user
            UUID tempUserId = tempUser.getId();
            UUID tempTaskId = tempTask.getId();

            userRepository.deleteById(tempUserId);

            // Verify task assignee is null
            TaskEntity taskAfterDeletion =
                    taskRepository.findById(tempTaskId).orElseThrow();
            assertNull(taskAfterDeletion.getAssignee());
        }

        @Test
        @Order(2)
        @Transactional
        void testProjectDeletionCascade() {
            // Create temporary test data
            ProjectEntity tempProject = createProject("Temp Project");
            UserProjectConnection tempConnection =
                    createUserProjectConnection(
                            secondaryUser,
                            tempProject,
                            UserRole.MEMBER
                    );
            TaskEntity tempTask = createTask(
                    "Temp Task",
                    tempProject,
                    secondaryUser
            );

            // Delete project
            UUID projectId = tempProject.getId();
            UUID connectionId = tempConnection.getId();
            UUID taskId = tempTask.getId();

            projectRepository.deleteById(projectId);

            // Verify cascade deletion
            assertFalse(projectRepository.existsById(projectId));
            assertFalse(connectionRepository.existsById(connectionId));
            assertFalse(taskRepository.existsById(taskId));
        }
    }
}