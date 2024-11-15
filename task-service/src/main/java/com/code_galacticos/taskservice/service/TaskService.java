package com.code_galacticos.taskservice.service;

import com.code_galacticos.taskservice.model.dto.task.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    public List<TaskResponseDto> getAllTasks(String userId, UUID projectId) {
        return Collections.emptyList();
    }

    public TaskResponseDto getTaskById(String userId, UUID projectId, UUID taskId) {
        return null;
    }

    public TaskResponseDto createTask(String userId, UUID projectId, TaskCreateDto taskCreateDto) {
        return null;
    }

    public TaskResponseDto updateTask(String userId, UUID projectId, UUID taskId , TaskUpdateDto taskUpdateDto) {
        return null;
    }

    public void deleteTask(String userId, UUID projectId, UUID taskId) {
    }

    public TaskResponseDto updateTaskPriority(String userId, UUID projectId, UUID taskId , UpdateTaskPriorityDto priorityDto) {
        return null;
    }

    public TaskResponseDto updateTaskAssignee(String userId, UUID projectId, UUID taskId , UpdateTaskAssigneeDto taskUpdateDto) {
        return null;
    }

}
