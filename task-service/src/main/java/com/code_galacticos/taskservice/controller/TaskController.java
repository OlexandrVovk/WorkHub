package com.code_galacticos.taskservice.controller;

import com.code_galacticos.taskservice.annotation.CurrentUser;
import com.code_galacticos.taskservice.model.entity.TaskEntity;
import com.code_galacticos.taskservice.model.entity.UserEntity;
import com.code_galacticos.taskservice.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/{projectId}")
    public ResponseEntity<List<TaskEntity>> getAllTasks(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(taskService.getAllTasks(projectId));
    }

    @PostMapping("/{projectId}")
    public ResponseEntity<TaskEntity> createTask(
            @PathVariable UUID projectId,
            @Valid @RequestBody TaskEntity taskEntity) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(taskEntity, projectId));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskEntity> getTaskById(
            @PathVariable UUID taskId) {
        return ResponseEntity.ok(taskService.getTaskById(taskId));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskEntity> updateTask(
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskEntity taskEntity) {
        return ResponseEntity.ok(taskService.updateTask(taskId, taskEntity));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable UUID taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{taskId}/priority")
    public ResponseEntity<TaskEntity> updateTaskPriority(
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskEntity taskEntity) {
        return ResponseEntity.ok(taskService.updateTaskPriority(taskId, taskEntity));
    }

    @PutMapping("/{taskId}/status")
    public ResponseEntity<TaskEntity> updateTaskStatus(
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskEntity taskEntity) {
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, taskEntity));
    }

    @PutMapping("/{taskId}/assignee")
    public ResponseEntity<TaskEntity> updateTaskAssignee(
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskEntity taskEntity) {
        return ResponseEntity.ok(taskService.updateTaskAssignee(taskId, taskEntity));
    }
}