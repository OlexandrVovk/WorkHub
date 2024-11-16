package com.code_galacticos.taskservice.service;

import com.code_galacticos.taskservice.model.entity.ProjectEntity;
import com.code_galacticos.taskservice.model.entity.TaskEntity;
import com.code_galacticos.taskservice.model.entity.UserEntity;
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


    public List<TaskEntity> getAllTasks(UUID projectId) {
        return taskRepository.findAllByProjectId(projectId);
    }

    public TaskEntity getTaskById(UUID projectId, UUID taskId) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        if (!task.getProject().getId().equals(projectId)) {
            throw new SecurityException("Task does not belong to the specified project");
        }

        return task;
    }

    public TaskEntity createTask(UUID projectId, TaskEntity taskEntity) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        taskEntity.setId(UUID.randomUUID());
        taskEntity.setProject(project);
        taskEntity.setReporter(taskEntity.getReporter());
        taskEntity.setStatus(taskEntity.getStatus());
        if (taskEntity.getPriority() == null) {
            taskEntity.setPriority(taskEntity.getPriority());
        }
        return taskRepository.save(taskEntity);
    }

    public TaskEntity updateTask(UUID userId, UUID projectId, UUID taskId , TaskEntity updatedTask) {
        TaskEntity existingTask = getTaskById(projectId, taskId);

        existingTask.setName(updatedTask.getName());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setDeadline(updatedTask.getDeadline());

        return taskRepository.save(existingTask);
    }

    public void deleteTask(UUID projectId, UUID taskId) {
        TaskEntity task = getTaskById(projectId, taskId);
        taskRepository.delete(task);
    }

    public TaskEntity updateTaskPriority(UUID userId, UUID projectId, UUID taskId , TaskEntity taskUpdate) {
        TaskEntity existingTask = getTaskById(projectId, taskId);
        existingTask.setPriority(taskUpdate.getPriority());

        return taskRepository.save(existingTask);
    }

    public TaskEntity updateTaskStatus(UUID userId, UUID projectId, UUID taskId , TaskEntity taskUpdate) {
        TaskEntity existingTask = getTaskById(projectId, taskId);
        existingTask.setStatus(taskUpdate.getStatus());

        return taskRepository.save(existingTask);
    }

    public TaskEntity updateTaskAssignee(UUID userId, UUID projectId, UUID taskId , TaskEntity taskUpdate) {
        TaskEntity existingTask = getTaskById(projectId, taskId);

        if (taskUpdate.getAssignee() != null) {
            UserEntity assignee = userRepository.findById(taskUpdate.getAssignee().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Assignee not found"));
//            boolean isAssigneeInProject = ProjectUserConnectionService.findAllByProjectId(projectId).stream()
//                    .anyMatch(connection -> connection.getUser().getId().equals(assignee.getId()));
//
//            if (!isAssigneeInProject) {
//                throw new IllegalArgumentException("Assignee is not a member of the project");
//            }

            existingTask.setAssignee(assignee);
        } else {
            existingTask.setAssignee(null);
        }
        return taskRepository.save(existingTask);
    }

}
