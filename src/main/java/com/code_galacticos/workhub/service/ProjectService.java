package com.code_galacticos.workhub.service;

import com.code_galacticos.workhub.model.dto.project.ProjectCreateDto;
import com.code_galacticos.workhub.model.dto.project.ProjectResponseDto;
import com.code_galacticos.workhub.model.dto.project.ProjectUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    public List<ProjectResponseDto> getAllProjects(UUID userId) {
        return Collections.emptyList();
    }

    public ProjectResponseDto getProjectById(UUID userId, UUID projectId) {
        return null;
    }

    public ProjectResponseDto createProject(UUID userId, ProjectCreateDto projectDto) {
        return null;
    }

    public ProjectResponseDto updateProject(UUID userId, UUID projectId, ProjectUpdateDto projectDto) {
        return null;
    }

    public void deleteProject(UUID userId, UUID projectId) {
    }
}
