package com.code_galacticos.taskservice.service;

import com.code_galacticos.taskservice.model.dto.project.ProjectCreateDto;
import com.code_galacticos.taskservice.model.dto.project.ProjectResponseDto;
import com.code_galacticos.taskservice.model.dto.project.ProjectUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    public List<ProjectResponseDto> getAllProjects(String userId) {
        return Collections.emptyList();
    }

    public ProjectResponseDto getProjectById(String userId, UUID projectId) {
        return null;
    }

    public ProjectResponseDto createProject(String userId, ProjectCreateDto projectDto) {
        return null;
    }

    public ProjectResponseDto updateProject(String userId, UUID projectId, ProjectUpdateDto projectDto) {
        return null;
    }

    public void deleteProject(String userId, UUID projectId) {
    }
}
