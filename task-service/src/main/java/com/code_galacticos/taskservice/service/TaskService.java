package com.code_galacticos.taskservice.service;

import com.code_galacticos.taskservice.model.entity.ProjectEntity;
import com.code_galacticos.taskservice.model.entity.TaskEntity;
import com.code_galacticos.taskservice.model.entity.UserEntity;
import com.code_galacticos.taskservice.rabbit.EmailNotificationMessage;
import com.code_galacticos.taskservice.rabbit.EmailNotificationSender;
import com.code_galacticos.taskservice.repository.ProjectRepository;
import com.code_galacticos.taskservice.repository.TaskRepository;
import com.code_galacticos.taskservice.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponse;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Tag(name = "Task Service", description = "Service for managing tasks within projects")
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final EmailNotificationSender emailNotificationSender;
    private final EmailTemplateService emailTemplateService; // Add this


    /**
     * Retrieves all tasks associated with a specific project.
     *
     * @param projectId UUID of the project
     * @return List of TaskEntity objects belonging to the project
     */
    @Operation(
            summary = "Get all project tasks",
            description = "Retrieves all tasks associated with a specific project"
    )
    public List<TaskEntity> getAllTasks(UUID projectId) {
        return taskRepository.findAllByProjectId(projectId);
    }

    /**
     * Retrieves a specific task by its ID.
     *
     * @param taskId UUID of the task to retrieve
     * @return TaskEntity containing task details
     * @throws EntityNotFoundException if task not found
     *
     * @apiNote This method only retrieves the task data, not its related entities
     */
    @Operation(
            summary = "Get task by ID",
            description = "Retrieves a specific task using its UUID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task found successfully",
                    content = @Content(schema = @Schema(implementation = TaskEntity.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public TaskEntity getTaskById(UUID taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
    }

    /**
     * Creates a new task within a project.
     * Sets default values for status and priority if not provided.
     *
     * @param userEntity User creating the task (will be set as reporter)
     * @param taskEntity Task details to create
     * @param projectId UUID of the project to create task in
     * @return Created TaskEntity
     * @throws EntityNotFoundException if project not found
     *
     * @apiNote Default status and priority will be assigned if not specified
     */
    @Operation(
            summary = "Create task",
            description = "Creates a new task in specified project"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Task created successfully",
                    content = @Content(schema = @Schema(implementation = TaskEntity.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public TaskEntity createTask(UserEntity userEntity, TaskEntity taskEntity, UUID projectId) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        taskEntity.setProject(project);
        taskEntity.setReporter(userEntity);
        taskEntity.setStatus(taskEntity.getStatus());
        if (taskEntity.getPriority() == null) {
            taskEntity.setPriority(taskEntity.getPriority());
        }
        return taskRepository.save(taskEntity);
    }

    /**
     * Updates task details including name, description, and deadline.
     * Other properties like status, priority, and assignee must be updated using their specific methods.
     *
     * @param taskId UUID of task to update
     * @param updatedTask Task details containing new values
     * @return Updated TaskEntity
     * @throws EntityNotFoundException if task not found
     *
     * @apiNote This method only updates basic task details. For status, priority, or assignee changes,
     * use their respective update methods
     * @see #updateTaskStatus(UUID, TaskEntity)
     * @see #updateTaskPriority(UUID, TaskEntity)
     * @see #updateTaskAssignee(UUID, TaskEntity)
     */
    @Operation(
            summary = "Update task details",
            description = "Updates basic task details (name, description, deadline)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task updated successfully",
                    content = @Content(schema = @Schema(implementation = TaskEntity.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public TaskEntity updateTask(UUID taskId , TaskEntity updatedTask) {
        TaskEntity existingTask = getTaskById(taskId);

        existingTask.setName(updatedTask.getName());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setDeadline(updatedTask.getDeadline());

        return taskRepository.save(existingTask);
    }

    /**
     * Permanently deletes a task from the system.
     *
     * @param taskId UUID of task to delete
     * @throws EntityNotFoundException if task not found
     *
     * @apiNote This operation cannot be undone. All task data will be permanently deleted.
     */
    @Operation(
            summary = "Delete task",
            description = "Permanently removes a task from the system"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Task deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public void deleteTask( UUID taskId) {
        TaskEntity task = getTaskById(taskId);
        taskRepository.delete(task);
    }

    /**
     * Updates task priority level.
     * This is a specialized update method that only modifies the priority of the task.
     *
     * @param taskId UUID of task to update
     * @param taskUpdate Task containing new priority value
     * @return Updated TaskEntity
     * @throws EntityNotFoundException if task not found
     *
     * @apiNote Only the priority field from the taskUpdate parameter is used
     */
    @Operation(
            summary = "Update task priority",
            description = "Updates only the priority of a task"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task priority updated successfully",
                    content = @Content(schema = @Schema(implementation = TaskEntity.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public TaskEntity updateTaskPriority(UUID taskId , TaskEntity taskUpdate) {
        TaskEntity existingTask = getTaskById(taskId);
        existingTask.setPriority(taskUpdate.getPriority());

        return taskRepository.save(existingTask);
    }

    /**
     * Updates task status.
     * This is a specialized update method that only modifies the status of the task.
     *
     * @param taskId UUID of task to update
     * @param taskUpdate Task containing new status value
     * @return Updated TaskEntity
     * @throws EntityNotFoundException if task not found
     *
     * @apiNote Only the status field from the taskUpdate parameter is used
     */
    @Operation(
            summary = "Update task status",
            description = "Updates only the status of a task"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task status updated successfully",
                    content = @Content(schema = @Schema(implementation = TaskEntity.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public TaskEntity updateTaskStatus(UUID taskId , TaskEntity taskUpdate) {
        TaskEntity existingTask = getTaskById(taskId);
        existingTask.setStatus(taskUpdate.getStatus());

        return taskRepository.save(existingTask);
    }

    /**
     * Updates task assignee and sends notification email to new assignee.
     *
     * @param taskId UUID of task to update
     * @param taskUpdate Task details containing new assignee
     * @return Updated TaskEntity
     * @throws EntityNotFoundException if task or assignee not found
     *
     * @apiNote Email notification will be sent to the new assignee
     * @see EmailTemplateService#createTaskAssignmentEmail
     */
    @Operation(
            summary = "Update task assignee",
            description = "Updates task assignee and sends notification"
    )
    public TaskEntity updateTaskAssignee(UUID taskId, TaskEntity taskUpdate) {
        TaskEntity existingTask = getTaskById(taskId);

        if (taskUpdate.getAssignee() != null) {
            UserEntity assignee = userRepository.findById(taskUpdate.getAssignee().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Assignee not found"));

            existingTask.setAssignee(assignee);

            // Use EmailTemplateService to create the notification
            EmailNotificationMessage emailNotificationMessage = emailTemplateService.createTaskAssignmentEmail(
                    assignee,
                    existingTask.getReporter(), // Assuming the reporter is the one assigning the task
                    existingTask,
                    existingTask.getProject()
            );
            emailNotificationSender.sendEmailNotification(emailNotificationMessage);
        } else {
            existingTask.setAssignee(null);
        }
        return taskRepository.save(existingTask);
    }
}
