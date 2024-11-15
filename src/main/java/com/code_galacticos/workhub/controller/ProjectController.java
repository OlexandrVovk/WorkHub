package com.code_galacticos.workhub.controller;

import com.code_galacticos.workhub.model.dto.project.ProjectCreateDto;
import com.code_galacticos.workhub.model.dto.project.ProjectResponseDto;
import com.code_galacticos.workhub.model.dto.project.ProjectUpdateDto;
import com.code_galacticos.workhub.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectResponseDto>> getAllProjects(
            @RequestHeader("user-id") UUID userId) {
        return ResponseEntity.ok(projectService.getAllProjects(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> getProjectById(
            @RequestHeader("user-id") UUID userId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(projectService.getProjectById(userId, id));
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDto> createProject(
            @RequestHeader("user-id") UUID userId,
            @Valid @RequestBody ProjectCreateDto projectDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.createProject(userId, projectDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> updateProject(
            @RequestHeader("user-id") UUID userId,
            @PathVariable UUID id,
            @Valid @RequestBody ProjectUpdateDto projectDto) {
        return ResponseEntity.ok(projectService.updateProject(userId, id, projectDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(
            @RequestHeader("user-id") UUID userId,
            @PathVariable UUID id) {
        projectService.deleteProject(userId, id);
        return ResponseEntity.noContent().build();
    }
//
//    @GetMapping("/{id}/dashboard")
//    public ResponseEntity<DashboardDto> getProjectDashboard(
//            @RequestHeader("user-id") UUID userId,
//            @PathVariable UUID id) {
//        return ResponseEntity.ok(projectService.getProjectDashboard(userId, id));
//    }
//
//    @GetMapping("/{id}/statistics")
//    public ResponseEntity<StatisticsDto> getProjectStatistics(
//            @RequestHeader("user-id") UUID userId,
//            @PathVariable UUID id) {
//        return ResponseEntity.ok(projectService.getProjectStatistics(userId, id));
//    }
//
//    @GetMapping("/search")
//    public ResponseEntity<List<ProjectResponseDto>> searchProjects(
//            @RequestHeader("user-id") UUID userId,
//            @RequestParam String query) {
//        return ResponseEntity.ok(projectService.searchProjects(userId, query));
//    }
}