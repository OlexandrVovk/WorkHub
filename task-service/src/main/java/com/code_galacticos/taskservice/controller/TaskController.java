package com.code_galacticos.taskservice.controller;

import com.code_galacticos.taskservice.annotation.CurrentUser;
import com.code_galacticos.taskservice.model.dto.task.*;
import com.code_galacticos.taskservice.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks(
            @CurrentUser UUID userId,
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(taskService.getAllTasks(userId, projectId));
    }

    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(
            @CurrentUser UUID userId,
            @PathVariable UUID projectId,
            @Valid @RequestBody TaskCreateDto taskDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(userId, projectId, taskDto));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponseDto> getTaskById(
            @CurrentUser UUID userId,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId) {
        return ResponseEntity.ok(taskService.getTaskById(userId, projectId, taskId));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @CurrentUser UUID userId,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskUpdateDto taskDto) {
        return ResponseEntity.ok(taskService.updateTask(userId, projectId, taskId, taskDto));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @CurrentUser UUID userId,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId) {
        taskService.deleteTask(userId, projectId, taskId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{taskId}/priority")
    public ResponseEntity<TaskResponseDto> updateTaskPriority(
            @CurrentUser UUID userId,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody UpdateTaskPriorityDto priorityDto) {
        return ResponseEntity.ok(taskService.updateTaskPriority(userId, projectId, taskId, priorityDto));
    }

    @PutMapping("/{taskId}/assignee")
    public ResponseEntity<TaskResponseDto> updateTaskAssignee(
            @CurrentUser UUID userId,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody UpdateTaskAssigneeDto assigneeDto) {
        return ResponseEntity.ok(taskService.updateTaskAssignee(userId, projectId, taskId, assigneeDto));
    }
}