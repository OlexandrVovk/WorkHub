package com.code_galacticos.taskservice.service;


import com.code_galacticos.taskservice.model.dto.team.AssignTaskDto;
import com.code_galacticos.taskservice.model.dto.team.TeamMemberDto;
import com.code_galacticos.taskservice.model.dto.team.UpdateRoleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public interface ProjectTeamService {
    List<TeamMemberDto> getProjectTeam(UUID userId, UUID projectId);

    TeamMemberDto addTeamMember(UUID userId, UUID projectId, TeamMemberDto memberDto);

    void removeTeamMember(UUID userId, UUID projectId, UUID memberId);

    TeamMemberDto updateMemberRole(UUID userId, UUID projectId, UUID memberId, UpdateRoleDto roleDto);

    List<AssignTaskDto> getMemberTasks(UUID userId, UUID projectId, UUID memberId);

    AssignTaskDto assignTask(UUID userId, UUID projectId, UUID memberId, UUID taskId);

    void unassignTask(UUID userId, UUID projectId, UUID memberId, UUID taskId);

    List<TeamMemberDto> getAvailableMembers(UUID userId, UUID projectId);

    TeamMemberDto getMemberWorkload(UUID userId, UUID projectId, UUID memberId);
}
