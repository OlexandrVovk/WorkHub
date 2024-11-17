package com.code_galacticos.taskservice.controller;

import com.code_galacticos.taskservice.annotation.CurrentUser;
import com.code_galacticos.taskservice.model.entity.UserEntity;
import com.code_galacticos.taskservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Create a new user",
            description = "Creates a new user in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = UserEntity.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            )
    })
    @PostMapping
    public ResponseEntity<UserEntity> createUser(
            @Parameter(description = "Current authenticated user")
            @CurrentUser UserEntity userEntity) {
        UserEntity createdUser = userService.createUser(userEntity);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves user details by UUID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User found",
                    content = @Content(schema = @Schema(implementation = UserEntity.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserEntity> getUser(
            @Parameter(description = "User UUID", required = true)
            @PathVariable UUID userId) {
        UserEntity user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Get user by email",
            description = "Retrieves user details by email address"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User found",
                    content = @Content(schema = @Schema(implementation = UserEntity.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @GetMapping("/by-email")
    public ResponseEntity<UserEntity> getUserByEmail(
            @Parameter(description = "User email address", required = true, example = "user@example.com")
            @RequestParam String email) {
        UserEntity user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Update user",
            description = "Updates an existing user's details"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = UserEntity.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @PutMapping("/{userId}")
    public ResponseEntity<UserEntity> updateUser(
            @Parameter(description = "User UUID", required = true)
            @PathVariable UUID userId,
            @Parameter(description = "Updated user details", required = true)
            @Valid @RequestBody UserEntity userEntity) {
        UserEntity updatedUser = userService.updateUser(userId, userEntity);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(
            summary = "Check if user exists",
            description = "Checks if a user exists by email address"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Check completed",
                    content = @Content(schema = @Schema(implementation = Boolean.class))
            )
    })
    @GetMapping("/exists")
    public ResponseEntity<Boolean> doesUserExist(
            @Parameter(description = "Email address to check", required = true, example = "user@example.com")
            @RequestParam String email) {
        boolean exists = userService.doesUserExist(email);
        return ResponseEntity.ok(exists);
    }
}