package com.code_galacticos.taskservice.service;

import com.code_galacticos.taskservice.exception.UserAlreadyExistsException;
import com.code_galacticos.taskservice.exception.UserNotFoundException;
import com.code_galacticos.taskservice.model.entity.UserEntity;
import com.code_galacticos.taskservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * Creates a new user if the email doesn't already exist
     *
     * @param userEntity User details to create
     * @return Created UserEntity
     * @throws UserAlreadyExistsException if email already exists
     */
    @Transactional
    public UserEntity createUser(UserEntity userEntity) {
        if (userRepository.existsByEmail(userEntity.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + userEntity.getEmail() + " already exists");
        }
        return userRepository.save(userEntity);
    }

    /**
     * Retrieves user by ID
     *
     * @param userId User's UUID
     * @return UserEntity
     * @throws UserNotFoundException if user not found
     */
    public UserEntity getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }

    /**
     * Retrieves user by email
     *
     * @param email User's email
     * @return UserEntity
     * @throws UserNotFoundException if user not found
     */
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    /**
     * Updates existing user
     *
     * @param userId ID of user to update
     * @param userDetails Updated user details
     * @return Updated UserEntity
     * @throws UserNotFoundException if user not found
     * @throws UserAlreadyExistsException if trying to update to an email that already exists
     */
    @Transactional
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
     * Deletes user by ID
     *
     * @param userId ID of user to delete
     * @throws UserNotFoundException if user not found
     */
    @Transactional
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }

    /**
     * Checks if user exists by email
     *
     * @param email Email to check
     * @return true if user exists, false otherwise
     */
    public boolean doesUserExist(String email) {
        return userRepository.existsByEmail(email);
    }
}
