package com.code_galacticos.taskservice.service;

import com.code_galacticos.taskservice.exception.UserAlreadyExistsException;
import com.code_galacticos.taskservice.exception.UserNotFoundException;
import com.code_galacticos.taskservice.model.entity.UserEntity;
import com.code_galacticos.taskservice.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.ErrorResponse;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Tag(name = "User Service", description = "Service for managing user accounts and profiles")
public class UserService {
    private final UserRepository userRepository;

    /**
     * Creates a new user in the system.
     * Validates that the email is unique before creation.
     *
     * @param userEntity User details for creation. Must contain:
     *                   - Email (unique)
     *                   - First name
     *                   - Last name
     *                   - Optional image URL
     * @return Created UserEntity with generated UUID
     * @throws UserAlreadyExistsException if a user with the same email already exists
     *
     * @apiNote The email address is used as a unique identifier for users in the system,
     * alongside the UUID
     *
     * Example:
     * {@code
     * UserEntity newUser = userService.createUser(
     *     UserEntity.builder()
     *         .email("john.doe@example.com")
     *         .firstName("John")
     *         .lastName("Doe")
     *         .build()
     * );
     * }
     */
    @Transactional
    @Operation(
            summary = "Create new user",
            description = "Creates a new user account with the provided details"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = UserEntity.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "User with this email already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public UserEntity createUser(UserEntity userEntity) {
        if (userRepository.existsByEmail(userEntity.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + userEntity.getEmail() + " already exists");
        }
        return userRepository.save(userEntity);
    }

    /**
     * Retrieves a user by their UUID.
     *
     * @param userId UUID of the user to retrieve
     * @return UserEntity containing user details
     * @throws UserNotFoundException if no user found with given ID
     *
     * @apiNote This method retrieves the complete user profile including all fields
     */
    @Operation(
            summary = "Get user by ID",
            description = "Retrieves user details using their UUID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User found successfully",
                    content = @Content(schema = @Schema(implementation = UserEntity.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public UserEntity getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email Email address to search for
     * @return UserEntity containing user details
     * @throws UserNotFoundException if no user found with given email
     *
     * @apiNote Email search is case-insensitive
     *
     * Example:
     * {@code
     * UserEntity user = userService.getUserByEmail("john.doe@example.com");
     * }
     */
    @Operation(
            summary = "Get user by email",
            description = "Retrieves user details using their email address"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User found successfully",
                    content = @Content(schema = @Schema(implementation = UserEntity.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    /**
     * Updates an existing user's profile information.
     * Validates email uniqueness if it's being changed.
     *
     * @param userId ID of user to update
     * @param userDetails Updated user information containing:
     *                    - Email (must be unique if changed)
     *                    - First name
     *                    - Last name
     *                    - Image URL (optional)
     * @return Updated UserEntity
     * @throws UserNotFoundException if user with given ID doesn't exist
     * @throws UserAlreadyExistsException if attempting to change email to one that's already in use
     *
     * @apiNote
     * - If email is being changed, system validates that new email is not already in use
     * - Fields that are null in userDetails will not overwrite existing values
     * - User's ID cannot be changed
     *
     * Example:
     * {@code
     * UserEntity updatedUser = userService.updateUser(
     *     userId,
     *     UserEntity.builder()
     *         .email("new.email@example.com")
     *         .firstName("Updated")
     *         .lastName("Name")
     *         .build()
     * );
     * }
     */
    @Transactional
    @Operation(
            summary = "Update user profile",
            description = "Updates an existing user's profile information"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = UserEntity.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Email already in use",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public UserEntity updateUser(UUID userId, UserEntity userDetails) {
        UserEntity existingUser = getUserById(userId);

        // Check if trying to update to an email that already exists
        if (!existingUser.getEmail().equals(userDetails.getEmail()) &&
                userRepository.existsByEmail(userDetails.getEmail())) {
            throw new UserAlreadyExistsException("Email already in use: " + userDetails.getEmail());
        }

        existingUser.setEmail(userDetails.getEmail());
        existingUser.setFirstName(userDetails.getFirstName());
        existingUser.setLastName(userDetails.getLastName());
        existingUser.setImageUrl(userDetails.getImageUrl());

        return userRepository.save(existingUser);
    }

    /**
     * Permanently deletes a user from the system.
     * This operation will:
     * - Remove user profile
     * - Remove user from all projects (handled by DB cascade)
     * - Remove user's task assignments (handled by DB cascade)
     *
     * @param userId UUID of user to delete
     * @throws UserNotFoundException if user with given ID doesn't exist
     *
     * @apiNote
     * - This operation cannot be undone
     * - Make sure to handle user's projects and tasks before deletion
     * - Consider using soft delete for production systems
     */
    @Transactional
    @Operation(
            summary = "Delete user",
            description = "Permanently removes a user from the system"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "User deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }

    /**
     * Checks if a user exists in the system by email address.
     *
     * @param email Email address to check
     * @return boolean indicating if user exists (true) or not (false)
     *
     * @apiNote
     * - Email check is case-insensitive
     * - This method does not throw exceptions for non-existent users
     *
     * Example:
     * {@code
     * if (userService.doesUserExist("john.doe@example.com")) {
     *     // Handle existing user case
     * }
     * }
     */
    @Operation(
            summary = "Check user existence",
            description = "Checks if a user exists by email address"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Check completed successfully",
                    content = @Content(schema = @Schema(implementation = Boolean.class))
            )
    })
    public boolean doesUserExist(String email) {
        return userRepository.existsByEmail(email);
    }
}
