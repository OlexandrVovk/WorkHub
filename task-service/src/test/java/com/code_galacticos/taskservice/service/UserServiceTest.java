package com.code_galacticos.taskservice.service;

import com.code_galacticos.taskservice.exception.UserAlreadyExistsException;
import com.code_galacticos.taskservice.exception.UserNotFoundException;
import com.code_galacticos.taskservice.model.entity.UserEntity;
import com.code_galacticos.taskservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail("test@example.com");
        userEntity.setFirstName("Test");
        userEntity.setLastName("User");
        userEntity.setImageUrl("http://example.com/image.jpg");
    }

    @Test
    void createUser_Success() {
        // Arrange
        when(userRepository.existsByEmail(userEntity.getEmail())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // Act
        UserEntity result = userService.createUser(userEntity);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void createUser_EmailExists() {
        // Arrange
        when(userRepository.existsByEmail(userEntity.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () ->
                userService.createUser(userEntity)
        );
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_Success() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        // Act
        UserEntity result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void getUserById_NotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () ->
                userService.getUserById(userId)
        );
    }

    @Test
    void getUserByEmail_Success() {
        // Arrange
        when(userRepository.findByEmail(userEntity.getEmail())).thenReturn(Optional.of(userEntity));

        // Act
        UserEntity result = userService.getUserByEmail(userEntity.getEmail());

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void getUserByEmail_NotFound() {
        // Arrange
        when(userRepository.findByEmail(userEntity.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () ->
                userService.getUserByEmail(userEntity.getEmail())
        );
    }

    @Test
    void updateUser_Success() {
        // Arrange
        UserEntity updatedUser = new UserEntity();
        updatedUser.setEmail("updated@example.com");
        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("User");
        updatedUser.setImageUrl("http://example.com/updated.jpg");

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRepository.existsByEmail(updatedUser.getEmail())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(updatedUser);

        // Act
        UserEntity result = userService.updateUser(userId, updatedUser);

        // Assert
        assertNotNull(result);
        assertEquals("updated@example.com", result.getEmail());
        assertEquals("Updated", result.getFirstName());
    }

    @Test
    void updateUser_EmailExists() {
        // Arrange
        UserEntity updatedUser = new UserEntity();
        updatedUser.setEmail("existing@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRepository.existsByEmail(updatedUser.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () ->
                userService.updateUser(userId, updatedUser)
        );
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_Success() {
        // Arrange
        when(userRepository.existsById(userId)).thenReturn(true);

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_NotFound() {
        // Arrange
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () ->
                userService.deleteUser(userId)
        );
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void doesUserExist_True() {
        // Arrange
        when(userRepository.existsByEmail(userEntity.getEmail())).thenReturn(true);

        // Act
        boolean result = userService.doesUserExist(userEntity.getEmail());

        // Assert
        assertTrue(result);
    }

    @Test
    void doesUserExist_False() {
        // Arrange
        when(userRepository.existsByEmail(userEntity.getEmail())).thenReturn(false);

        // Act
        boolean result = userService.doesUserExist(userEntity.getEmail());

        // Assert
        assertFalse(result);
    }
}