package com.code_galacticos.taskservice.controller;

import com.code_galacticos.taskservice.annotation.CurrentUser;
import com.code_galacticos.taskservice.model.entity.UserEntity;
import com.code_galacticos.taskservice.service.ProjectUserConnectionService;
import com.code_galacticos.taskservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class ProjectConnectionController {

    private final ProjectUserConnectionService projectUserConnectionService;

    @PostMapping
    public ResponseEntity<UserEntity> addUser(
            @Valid @RequestBody UserEntity userEntity) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectUserConnectionService.addUser(userEntity));
    }

    @GetMapping
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        return ResponseEntity.ok(projectUserConnectionService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(
            @CurrentUser UUID userId) {
        return ResponseEntity.ok(projectUserConnectionService.getUserById(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserEntity> removeUser(
            @CurrentUser UUID userId) {
        return ResponseEntity.ok(projectUserConnectionService.removeUser(userId));
    }

}