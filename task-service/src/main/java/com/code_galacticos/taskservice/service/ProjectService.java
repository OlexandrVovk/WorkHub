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
import com.code_galacticos.taskservice.repository.TaskRepository;
import com.code_galacticos.taskservice.repository.UserProjectConnectionRepository;
import com.code_galacticos.taskservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserProjectConnectionRepository userProjectConnectionRepository;
    private final UserRepository userRepository;
    private final EmailNotificationSender emailNotificationSender;
    private final EmailTemplateService emailTemplateService; // Add this

    /**
     * Creates a new project and establishes UserProjectConnection with OWNER role
     *
     * @param projectEntity Project details to create
     * @param creator User creating the project
     * @return Created ProjectEntity
     * @throws UserNotFoundException if creator user not found
     */
    @Transactional
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
     * Retrieves project by ID
     *
     * @param projectId Project's UUID
     * @return ProjectEntity
     * @throws ProjectNotFoundException if project not found
     */
    public ProjectEntity getProjectById(UUID projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId));
    }

    /**
     * Updates existing project
     * Only updates the project entity itself, doesn't modify any connections
     *
     * @param projectEntity Updated project entity
     * @return Updated ProjectEntity
     * @throws ProjectNotFoundException if project not found
     */
    public ProjectEntity updateProject(ProjectEntity projectEntity) {
        // Verify project exists
        if (!projectRepository.existsById(projectEntity.getId())) {
            throw new ProjectNotFoundException("Project not found with id: " + projectEntity.getId());
        }
        return projectRepository.save(projectEntity);
    }

    /**
     * Deletes project and all related data (tasks and user connections)
     *
     * @param projectId ID of project to delete
     * @throws ProjectNotFoundException if project not found
     */
    public void deleteProject(UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException("Project not found with id: " + projectId);
        }

        // Delete all related tasks first
        taskRepository.deleteAllByProjectId(projectId);

        // Delete all user-project connections
        userProjectConnectionRepository.deleteAllByProjectId(projectId);

        // Finally delete the project
        projectRepository.deleteByProjectId(projectId);
    }


}