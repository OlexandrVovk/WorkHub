package com.code_galacticos.taskservice.controller;

import com.code_galacticos.taskservice.annotation.CurrentUser;
import com.code_galacticos.taskservice.model.entity.TaskEntity;
import com.code_galacticos.taskservice.model.entity.UserEntity;
import com.code_galacticos.taskservice.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Task Management", description = "APIs for managing tasks within projects")
public class TaskController {

    private final TaskService taskService;

    @Operation(
            summary = "Get all tasks for a project",
            description = "Retrieves all tasks associated with the specified project"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved tasks",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = TaskEntity.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found"
            )
    })
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskEntity>> getAllTasks(
            @Parameter(description = "Project UUID", required = true)
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(taskService.getAllTasks(projectId));
    }

    @Operation(
            summary = "Create a new task",
            description = "Creates a new task in the specified project"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Task created successfully",
                    content = @Content(schema = @Schema(implementation = TaskEntity.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found"
            )
    })
    @PostMapping("/{projectId}")
    public ResponseEntity<TaskEntity> createTask(
            @Parameter(description = "Current authenticated user")
            @CurrentUser UserEntity user,
            @Parameter(description = "Project UUID", required = true)
            @PathVariable UUID projectId,
            @Parameter(description = "Task details", required = true)
            @Valid @RequestBody TaskEntity taskEntity) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(user, taskEntity, projectId));
    }

    @Operation(
            summary = "Get task by ID",
            description = "Retrieves a specific task by its UUID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Task found",
                    content = @Content(schema = @Schema(implementation = TaskEntity.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found"
            )
    })
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskEntity> getTaskById(
            @Parameter(description = "Task UUID", required = true)
            @PathVariable UUID taskId) {
        return ResponseEntity.ok(taskService.getTaskById(taskId));
    }

    @Operation(
            summary = "Update task priority",
            description = "Updates the priority of an existing task"
    )
    @PutMapping("/{taskId}/priority")
    public ResponseEntity<TaskEntity> updateTaskPriority(
            @Parameter(description = "Task UUID", required = true)
            @PathVariable UUID taskId,
            @Parameter(description = "Updated task details with new priority", required = true)
            @Valid @RequestBody TaskEntity taskEntity) {
        return ResponseEntity.ok(taskService.updateTaskPriority(taskId, taskEntity));
    }

    @Operation(
            summary = "Update task status",
            description = "Updates the status of an existing task"
    )
    @PutMapping("/{taskId}/status")
    public ResponseEntity<TaskEntity> updateTaskStatus(
            @Parameter(description = "Task UUID", required = true)
            @PathVariable UUID taskId,
            @Parameter(description = "Updated task details with new status", required = true)
            @Valid @RequestBody TaskEntity taskEntity) {
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, taskEntity));
    }

    @Operation(
            summary = "Update task assignee",
            description = "Updates the assignee of an existing task"
    )
    @PutMapping("/{taskId}/assignee")
    public ResponseEntity<TaskEntity> updateTaskAssignee(
            @Parameter(description = "Task UUID", required = true)
            @PathVariable UUID taskId,
            @Parameter(description = "Updated task details with new assignee", required = true)
            @Valid @RequestBody TaskEntity taskEntity) {
        return ResponseEntity.ok(taskService.updateTaskAssignee(taskId, taskEntity));
    }
}