package com.code_galacticos.taskservice.controller;

import com.code_galacticos.taskservice.annotation.CurrentUser;
import com.code_galacticos.taskservice.model.entity.UserEntity;
import com.code_galacticos.taskservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Create a new user
     *
     * @param userEntity User details
     * @return Created user
     */
    @PostMapping
    public ResponseEntity<UserEntity> createUser(
            @CurrentUser UserEntity userEntity) {
        UserEntity createdUser = userService.createUser(userEntity);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Get user by ID
     *
     * @param userId User UUID
     * @return User if found
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserEntity> getUser(@PathVariable UUID userId) {
        UserEntity user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * Get user by email
     *
     * @param email User email
     * @return User if found
     */
    @GetMapping("/by-email")
    public ResponseEntity<UserEntity> getUserByEmail(@RequestParam String email) {
        UserEntity user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    /**
     * Update existing user
     *
     * @param userId User UUID
     * @param userEntity Updated user details
     * @return Updated user
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserEntity> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UserEntity userEntity) {
        UserEntity updatedUser = userService.updateUser(userId, userEntity);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Delete user
     *
     * @param userId User UUID
     * @return No content on success
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Check if user exists by email
     *
     * @param email User email to check
     * @return Boolean indicating if user exists
     */
    @GetMapping("/exists")
    public ResponseEntity<Boolean> doesUserExist(@RequestParam String email) {
        boolean exists = userService.doesUserExist(email);
        return ResponseEntity.ok(exists);
    }
}