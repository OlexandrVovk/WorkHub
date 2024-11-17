package com.code_galacticos.taskservice.service;

import com.code_galacticos.taskservice.model.entity.ProjectEntity;
import com.code_galacticos.taskservice.model.entity.TaskEntity;
import com.code_galacticos.taskservice.model.entity.UserEntity;
import com.code_galacticos.taskservice.rabbit.EmailNotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailTemplateService {

    private static final String COMPANY_NAME = "Code Galacticos";

    /**
     * Creates email notification for when a user is added to a project
     */
    public EmailNotificationMessage createProjectAdditionEmail(UserEntity addedUser,
                                                               UserEntity projectOwner,
                                                               ProjectEntity project) {
        String subject = String.format("[%s] You've been added to project: %s",
                COMPANY_NAME,
                project.getName());

        String body = String.format("""
                Dear %s,
                
                You have been added to the project '%s' by %s %s.
                
                Project Details:
                - Name: %s
                - Description: %s
                
                You can now access the project and start collaborating with the team.
                
                Best regards,
                %s Team""",
                addedUser.getFirstName(),
                project.getName(),
                projectOwner.getFirstName(),
                projectOwner.getLastName(),
                project.getName(),
                project.getProjectDescription(),
                COMPANY_NAME);

        return EmailNotificationMessage.builder()
                .to(addedUser.getEmail())
                .subject(subject)
                .text(body)
                .build();
    }

    /**
     * Creates email notification for when a user is removed from a project
     */
    public EmailNotificationMessage createProjectRemovalEmail(UserEntity removedUser,
                                                              UserEntity projectOwner,
                                                              ProjectEntity project) {
        String subject = String.format("[%s] Your access to project '%s' has been removed",
                COMPANY_NAME,
                project.getName());

        String body = String.format("""
                Dear %s,
                
                Your access to the project '%s' has been removed by %s %s.
                
                If you believe this is a mistake, please contact the project owner.
                
                Best regards,
                %s Team""",
                removedUser.getFirstName(),
                project.getName(),
                projectOwner.getFirstName(),
                projectOwner.getLastName(),
                COMPANY_NAME);

        return EmailNotificationMessage.builder()
                .to(removedUser.getEmail())
                .subject(subject)
                .text(body)
                .build();
    }

    /**
     * Creates email notification for when a task is assigned to a user
     */
    public EmailNotificationMessage createTaskAssignmentEmail(UserEntity assignedUser,
                                                              UserEntity assignedBy,
                                                              TaskEntity task,
                                                              ProjectEntity project) {
        String subject = String.format("[%s] New task assigned to you in project: %s",
                COMPANY_NAME,
                project.getName());

        String body = String.format("""
                Dear %s,
                
                A new task has been assigned to you by %s %s in the project '%s'.
                
                Task Details:
                - Name: %s
                - Description: %s
                - Priority: %s
                - Deadline: %s
                
                Please review the task details and begin working on it at your earliest convenience.
                
                Best regards,
                %s Team""",
                assignedUser.getFirstName(),
                assignedBy.getFirstName(),
                assignedBy.getLastName(),
                project.getName(),
                task.getTaskName(),
                task.getTaskDescription(),
                task.getPriority(),
                task.getDeadline(),
                COMPANY_NAME);

        return EmailNotificationMessage.builder()
                .to(assignedUser.getEmail())
                .subject(subject)
                .text(body)
                .build();
    }
}