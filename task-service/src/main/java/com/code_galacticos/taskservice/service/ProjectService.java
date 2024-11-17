package com.code_galacticos.taskservice.service;

import com.code_galacticos.taskservice.exception.ProjectNotFoundException;
import com.code_galacticos.taskservice.exception.UserNotFoundException;
import com.code_galacticos.taskservice.exception.UserProjectConnectionException;
import com.code_galacticos.taskservice.model.entity.ProjectEntity;
import com.code_galacticos.taskservice.model.entity.UserEntity;
import com.code_galacticos.taskservice.model.entity.UserProjectConnection;
import com.code_galacticos.taskservice.model.enums.ProjectStatus;
import com.code_galacticos.taskservice.model.enums.UserRole;
import com.code_galacticos.taskservice.rabbit.EmailNotificationMessage;
import com.code_galacticos.taskservice.rabbit.EmailNotificationSender;
import com.code_galacticos.taskservice.repository.ProjectRepository;
import com.code_galacticos.taskservice.repository.TaskRepository;
import com.code_galacticos.taskservice.repository.UserProjectConnectionRepository;
import com.code_galacticos.taskservice.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.ErrorResponse;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Tag(name = "Project Service", description = "Service for managing projects")
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserProjectConnectionRepository userProjectConnectionRepository;
    private final UserRepository userRepository;
    private final EmailNotificationSender emailNotificationSender;
    private final EmailTemplateService emailTemplateService; // Add this

    /**
     * Creates a new project and establishes UserProjectConnection with OWNER role.
     * After creation, sends email notifications to the project owner.
     *
     * @param projectEntity Project details to create. Must contain name and description.
     * @param creator User who is creating the project. Will be assigned OWNER role.
     * @return Created ProjectEntity with generated ID and established connections
     * @throws UserNotFoundException if creator user not found in the system
     *
     * @apiNote This method handles both project creation and initial owner connection setup
     * @see UserProjectConnection
     * @see EmailTemplateService#createProjectCreationEmail
     */
    @Transactional
    @Operation(
            summary = "Create new project",
            description = "Creates a new project and assigns creator as owner"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Project successfully created",
                    content = @Content(schema = @Schema(implementation = ProjectEntity.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Creator user not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ProjectEntity createProject(ProjectEntity projectEntity, UserEntity creator) {
        // Save the project
        UUID projectId = UUID.randomUUID();
        projectEntity.setId(projectId);
        ProjectEntity savedProject = projectRepository.save(projectEntity);

        // Create UserProjectConnection with OWNER role
        UserProjectConnection connection = new UserProjectConnection();
        connection.setId(UUID.randomUUID());
        connection.setProject(savedProject);
        connection.setUser(creator);
        connection.setRole(UserRole.OWNER);
        userProjectConnectionRepository.save(connection);

        // Send project creation notification to the creator
        EmailNotificationMessage creationMessage = emailTemplateService.createProjectCreationEmail(
                creator,
                savedProject
        );
        emailNotificationSender.sendEmailNotification(creationMessage);

        // Send project addition notification
        EmailNotificationMessage additionMessage = emailTemplateService.createProjectAdditionEmail(
                creator,
                creator,
                savedProject
        );
        emailNotificationSender.sendEmailNotification(additionMessage);

        return savedProject;
    }


    /**
     * Retrieves a project by its unique identifier.
     *
     * @param projectId UUID of the project to retrieve
     * @return ProjectEntity containing project details
     * @throws ProjectNotFoundException if no project found with given ID
     */
    @Operation(
            summary = "Get project by ID",
            description = "Retrieves project details using its UUID"
    )
    public ProjectEntity getProjectById(UUID projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId));
    }

    /**
     * Updates an existing project's details.
     * Only updates the project entity itself, doesn't modify any user connections.
     *
     * @param projectEntity Updated project details. Must contain valid project ID.
     * @return Updated ProjectEntity
     * @throws ProjectNotFoundException if no project exists with the given ID
     *
     * @apiNote This method only updates basic project details (name, description, status)
     * For managing project users, see {@link ProjectUserConnectionService}
     */
    @Operation(
            summary = "Update project",
            description = "Updates basic project details without modifying user connections"
    )
    public ProjectEntity updateProject(ProjectEntity projectEntity) {
        // Verify project exists
        if (!projectRepository.existsById(projectEntity.getId())) {
            throw new ProjectNotFoundException("Project not found with id: " + projectEntity.getId());
        }
        return projectRepository.save(projectEntity);
    }

    /**
     * Deletes a project and all its related data.
     * This includes:
     * - All tasks within the project
     * - All user-project connections
     * - The project entity itself
     *
     * @param projectId UUID of the project to delete
     * @throws ProjectNotFoundException if no project found with given ID
     *
     * @apiNote This operation cannot be undone. All associated data will be permanently deleted.
     */
    @Transactional
    @Operation(
            summary = "Delete project",
            description = "Deletes project and all associated data (tasks, user connections)"
    )
    public void deleteProject(UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException("Project not found with id: " + projectId);
        }

//        // Delete all related tasks first
//        taskRepository.deleteAllByProjectId(projectId);
//
//        // Delete all user-project connections
//        userProjectConnectionRepository.deleteAllByProjectId(projectId);

        // Finally delete the project
        projectRepository.deleteByProjectId(projectId);
    }

    /**
     * Updates the status of an existing project.
     *
     * @param projectId UUID of the project to update
     * @param newStatus New status to set for the project
     * @return Updated ProjectEntity
     * @throws ProjectNotFoundException if no project exists with the given ID
     */
    @Transactional
    @Operation(
            summary = "Update project status",
            description = "Updates only the status of an existing project"
    )
    public ProjectEntity updateProjectStatus(UUID projectId, ProjectStatus newStatus) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId));

        project.setStatus(newStatus);

        // Send status update notification to all project members
        List<UserProjectConnection> projectMembers = userProjectConnectionRepository.findAllByProjectId(projectId);

        return projectRepository.save(project);
    }


}