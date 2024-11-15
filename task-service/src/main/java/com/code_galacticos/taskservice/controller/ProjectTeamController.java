package com.code_galacticos.taskservice.controller;

import com.code_galacticos.taskservice.annotation.CurrentUser;
import com.code_galacticos.taskservice.model.dto.team.AssignTaskDto;
import com.code_galacticos.taskservice.model.dto.team.TeamMemberDto;
import com.code_galacticos.taskservice.model.dto.team.UpdateRoleDto;
import com.code_galacticos.taskservice.service.ProjectTeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/members")
@RequiredArgsConstructor
public class ProjectTeamController {

    private final ProjectTeamService projectTeamService;

    @GetMapping
    public ResponseEntity<List<TeamMemberDto>> getProjectTeam(
            @CurrentUser UUID userId,
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(projectTeamService.getProjectTeam(userId, projectId));
    }

    @PostMapping
    public ResponseEntity<TeamMemberDto> addTeamMember(
            @CurrentUser UUID userId,
            @PathVariable UUID projectId,
            @Valid @RequestBody TeamMemberDto memberDto) {
        return ResponseEntity.ok(projectTeamService.addTeamMember(userId, projectId, memberDto));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> removeTeamMember(
            @CurrentUser UUID userId,
            @PathVariable UUID projectId,
            @PathVariable UUID memberId) {
        projectTeamService.removeTeamMember(userId, projectId, memberId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{memberId}/role")
    public ResponseEntity<TeamMemberDto> updateMemberRole(
            @CurrentUser UUID userId,
            @PathVariable UUID projectId,
            @PathVariable UUID memberId,
            @Valid @RequestBody UpdateRoleDto roleDto) {
        return ResponseEntity.ok(projectTeamService.updateMemberRole(userId, projectId, memberId, roleDto));
    }

    @GetMapping("/{memberId}/tasks")
    public ResponseEntity<List<AssignTaskDto>> getMemberTasks(
            @CurrentUser UUID userId,
            @PathVariable UUID projectId,
            @PathVariable UUID memberId) {
        return ResponseEntity.ok(projectTeamService.getMemberTasks(userId, projectId, memberId));
    }

    @PostMapping("/{memberId}/tasks/{taskId}")
    public ResponseEntity<AssignTaskDto> assignTask(
            @CurrentUser UUID userId,
            @PathVariable UUID projectId,
            @PathVariable UUID memberId,
            @PathVariable UUID taskId) {
        return ResponseEntity.ok(projectTeamService.assignTask(userId, projectId, memberId, taskId));
    }

    @DeleteMapping("/{memberId}/tasks/{taskId}")
    public ResponseEntity<Void> unassignTask(
            @CurrentUser UUID userId,
            @PathVariable UUID projectId,
            @PathVariable UUID memberId,
            @PathVariable UUID taskId) {
        projectTeamService.unassignTask(userId, projectId, memberId, taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/available")
    public ResponseEntity<List<TeamMemberDto>> getAvailableMembers(
            @CurrentUser UUID userId,
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(projectTeamService.getAvailableMembers(userId, projectId));
    }

    @GetMapping("/{memberId}/workload")
    public ResponseEntity<TeamMemberDto> getMemberWorkload(
            @CurrentUser UUID userId,
            @PathVariable UUID projectId,
            @PathVariable UUID memberId) {
        return ResponseEntity.ok(projectTeamService.getMemberWorkload(userId, projectId, memberId));
    }
}