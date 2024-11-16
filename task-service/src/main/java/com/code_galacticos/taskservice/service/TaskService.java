package com.code_galacticos.taskservice.service;

import com.code_galacticos.taskservice.model.entity.TaskEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    public List<TaskEntity> getAllTasks(UUID userId, UUID projectId) {
        return Collections.emptyList();
    }

    public TaskEntity getTaskById(UUID userId, UUID projectId, UUID taskId) {
        return null;
    }

    public TaskEntity createTask(UUID userId, UUID projectId, TaskEntity TaskEntity) {
        return null;
    }

    public TaskEntity updateTask(UUID userId, UUID projectId, UUID taskId , TaskEntity TaskEntity) {
        return null;
    }

    public void deleteTask(UUID userId, UUID projectId, UUID taskId) {
    }

    public TaskEntity updateTaskPriority(UUID userId, UUID projectId, UUID taskId , TaskEntity TaskEntity) {
        return null;
    }

    public TaskEntity updateTaskStatus(UUID userId, UUID projectId, UUID taskId , TaskEntity TaskEntity) {
        return null;
    }

    public TaskEntity updateTaskAssignee(UUID userId, UUID projectId, UUID taskId , TaskEntity TaskEntity) {
        return null;
    }

}
