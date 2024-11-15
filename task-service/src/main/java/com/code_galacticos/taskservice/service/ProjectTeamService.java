package com.code_galacticos.taskservice.service;


import com.code_galacticos.taskservice.model.dto.team.AssignTaskDto;
import com.code_galacticos.taskservice.model.dto.team.TeamMemberDto;
import com.code_galacticos.taskservice.model.dto.team.UpdateRoleDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectTeamService {
    public List<TeamMemberDto> getProjectTeam(UUID userId, UUID projectId){
        return null;
    }

    public TeamMemberDto addTeamMember(UUID userId, UUID projectId, TeamMemberDto memberDto){
        return null;
    };

    public void removeTeamMember(UUID userId, UUID projectId, UUID memberId){
    }

    public TeamMemberDto updateMemberRole(UUID userId, UUID projectId, UUID memberId, UpdateRoleDto roleDto){
        return null;
    }

    public List<AssignTaskDto> getMemberTasks(UUID userId, UUID projectId, UUID memberId){
        return null;
    }

    public AssignTaskDto assignTask(UUID userId, UUID projectId, UUID memberId, UUID taskId){
        return null;
    }

    public void unassignTask(UUID userId, UUID projectId, UUID memberId, UUID taskId){
    }

    public List<TeamMemberDto> getAvailableMembers(UUID userId, UUID projectId){
        return null;
    }

    public TeamMemberDto getMemberWorkload(UUID userId, UUID projectId, UUID memberId){
        return null;
    }
}
