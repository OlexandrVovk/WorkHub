package com.code_galacticos.taskservice.service;

import com.code_galacticos.taskservice.model.entity.ProjectEntity;
import com.code_galacticos.taskservice.model.entity.TaskEntity;
import com.code_galacticos.taskservice.model.entity.UserEntity;
import com.code_galacticos.taskservice.rabbit.EmailNotificationMessage;
import com.code_galacticos.taskservice.rabbit.EmailNotificationSender;
import com.code_galacticos.taskservice.repository.ProjectRepository;
import com.code_galacticos.taskservice.repository.TaskRepository;
import com.code_galacticos.taskservice.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final EmailNotificationSender emailNotificationSender;
    private final EmailTemplateService emailTemplateService; // Add this



    public List<TaskEntity> getAllTasks(UUID projectId) {
        return taskRepository.findAllByProjectId(projectId);
    }

    public TaskEntity getTaskById(UUID taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
    }

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

    public TaskEntity updateTask(UUID taskId , TaskEntity updatedTask) {
        TaskEntity existingTask = getTaskById(taskId);

        existingTask.setName(updatedTask.getName());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setDeadline(updatedTask.getDeadline());

        return taskRepository.save(existingTask);
    }


    public void deleteTask( UUID taskId) {
        TaskEntity task = getTaskById(taskId);
        taskRepository.delete(task);
    }

    public TaskEntity updateTaskPriority(UUID taskId , TaskEntity taskUpdate) {
        TaskEntity existingTask = getTaskById(taskId);
        existingTask.setPriority(taskUpdate.getPriority());

        return taskRepository.save(existingTask);
    }

    public TaskEntity updateTaskStatus(UUID taskId , TaskEntity taskUpdate) {
        TaskEntity existingTask = getTaskById(taskId);
        existingTask.setStatus(taskUpdate.getStatus());

        return taskRepository.save(existingTask);
    }
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
