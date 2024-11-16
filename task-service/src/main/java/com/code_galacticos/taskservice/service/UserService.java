package com.code_galacticos.taskservice.service;

import com.code_galacticos.taskservice.model.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    public List<UserEntity> getAllUsers() {
        return Collections.emptyList();
    }

    public UserEntity getUserById(UUID userId) {
        return null;
    }

    public UserEntity createUser(UserEntity userEntity) {
        return null;
    }

}
