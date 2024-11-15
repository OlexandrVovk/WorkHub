package com.code_galacticos.taskservice.service;


import com.code_galacticos.taskservice.model.dto.team.AssignTaskDto;
import com.code_galacticos.taskservice.model.dto.team.TeamMemberDto;
import com.code_galacticos.taskservice.model.dto.team.UpdateRoleDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectTeamService {
    public List<TeamMemberDto> getProjectTeam(String userId, UUID projectId){
        return null;
    }

    public TeamMemberDto addTeamMember(String userId, UUID projectId, TeamMemberDto memberDto){
        return null;
    };

    public void removeTeamMember(String userId, UUID projectId, UUID memberId){
    }

    public TeamMemberDto updateMemberRole(String userId, UUID projectId, UUID memberId, UpdateRoleDto roleDto){
        return null;
    }

    public List<AssignTaskDto> getMemberTasks(String userId, UUID projectId, UUID memberId){
        return null;
    }

    public AssignTaskDto assignTask(String userId, UUID projectId, UUID memberId, UUID taskId){
        return null;
    }

    public void unassignTask(String userId, UUID projectId, UUID memberId, UUID taskId){
    }

    public List<TeamMemberDto> getAvailableMembers(String userId, UUID projectId){
        return null;
    }

    public TeamMemberDto getMemberWorkload(String userId, UUID projectId, UUID memberId){
        return null;
    }
}
