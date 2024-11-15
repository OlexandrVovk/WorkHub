package com.code_galacticos.taskservice.service;

import com.code_galacticos.taskservice.model.dto.project.ProjectCreateDto;
import com.code_galacticos.taskservice.model.dto.project.ProjectResponseDto;
import com.code_galacticos.taskservice.model.dto.project.ProjectUpdateDto;
import com.code_galacticos.taskservice.repository.ProjectRepository;
import com.code_galacticos.taskservice.repository.TaskRepository;
import com.code_galacticos.taskservice.repository.UserProjectConnectionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserProjectConnectionRepository connectionRepository;

    public void deleteProject(UUID projectId) {
        // First delete all associated tasks
        taskRepository.deleteAllByProjectId(projectId);

        // Then delete all user-project connections
        connectionRepository.deleteAllByProjectId(projectId);

        // Finally delete the project
        projectRepository.deleteById(projectId);
    }

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
