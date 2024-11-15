package com.code_galacticos.taskservice.controller;

import com.code_galacticos.taskservice.annotation.CurrentUser;
import com.code_galacticos.taskservice.model.dto.project.ProjectCreateDto;
import com.code_galacticos.taskservice.model.dto.project.ProjectResponseDto;
import com.code_galacticos.taskservice.model.dto.project.ProjectUpdateDto;
import com.code_galacticos.taskservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v2/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectResponseDto>> getAllProjects(
            @CurrentUser UUID userId) {
        return ResponseEntity.ok(projectService.getAllProjects(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> getProjectById(
            @CurrentUser UUID userId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(projectService.getProjectById(userId, id));
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDto> createProject(
            @CurrentUser UUID userId,
            @Valid @RequestBody ProjectCreateDto projectDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.createProject(userId, projectDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> updateProject(
            @CurrentUser UUID userId,
            @PathVariable UUID id,
            @Valid @RequestBody ProjectUpdateDto projectDto) {
        return ResponseEntity.ok(projectService.updateProject(userId, id, projectDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(
            @CurrentUser UUID userId,
            @PathVariable UUID id) {
        projectService.deleteProject(userId, id);
        return ResponseEntity.noContent().build();
    }
}