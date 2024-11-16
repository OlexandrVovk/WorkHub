package com.code_galacticos.taskservice.controller;

import com.code_galacticos.taskservice.annotation.CurrentUser;
import com.code_galacticos.taskservice.model.entity.TaskEntity;
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
    public ResponseEntity<List<TaskEntity>> getAllTasks(
            @CurrentUser UUID userId,
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(taskService.getAllTasks(userId, projectId));
    }

    @PostMapping
    public ResponseEntity<TaskEntity> createTask(
            @CurrentUser UUID userId,
            @PathVariable UUID projectId,
            @Valid @RequestBody TaskEntity taskEntity) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(userId, projectId, taskEntity));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskEntity> getTaskById(
            @CurrentUser UUID userId,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId) {
        return ResponseEntity.ok(taskService.getTaskById(userId, projectId, taskId));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskEntity> updateTask(
            @CurrentUser UUID userId,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskEntity taskEntity) {
        return ResponseEntity.ok(taskService.updateTask(userId, projectId, taskId, taskEntity));
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
    public ResponseEntity<TaskEntity> updateTaskPriority(
            @CurrentUser UUID userId,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskEntity taskEntity) {
        return ResponseEntity.ok(taskService.updateTaskPriority(userId, projectId, taskId, taskEntity));
    }

    @PutMapping("/{taskId}/status")
    public ResponseEntity<TaskEntity> updateTaskStatus(
            @CurrentUser UUID userId,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskEntity taskEntity) {
        return ResponseEntity.ok(taskService.updateTaskStatus(userId, projectId, taskId, taskEntity));
    }

    @PutMapping("/{taskId}/assignee")
    public ResponseEntity<TaskEntity> updateTaskAssignee(
            @CurrentUser UUID userId,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskEntity taskEntity) {
        return ResponseEntity.ok(taskService.updateTaskAssignee(userId, projectId, taskId, taskEntity));
    }
}