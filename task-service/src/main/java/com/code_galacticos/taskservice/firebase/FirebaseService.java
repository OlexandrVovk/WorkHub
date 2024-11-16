package com.code_galacticos.taskservice.firebase;
import com.code_galacticos.taskservice.exception.TokenExpiredException;
import com.code_galacticos.taskservice.model.entity.UserEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class FirebaseService {

    public UserEntity verifyToken(String token) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            String uid = decodedToken.getUid();

            UserRecord userRecord = FirebaseAuth.getInstance().getUser(uid);

            return UserEntity.builder()
                    .id(UUID.nameUUIDFromBytes(uid.getBytes()))
                    .email(userRecord.getEmail())
                    .firstName(userRecord.getDisplayName() != null ?
                            userRecord.getDisplayName().split(" ")[0] : "")
                    .lastName(userRecord.getDisplayName() != null &&
                            userRecord.getDisplayName().split(" ").length > 1 ?
                            userRecord.getDisplayName().split(" ")[1] : "")
                    .imageUrl(userRecord.getPhotoUrl())
                    .build();

        } catch (FirebaseAuthException e) {
            log.error("Firebase token verification failed", e);
            if (e.getMessage().contains("expired")) {
                throw new TokenExpiredException("Token has expired. Please refresh your token.");
            }
            throw new RuntimeException("Invalid token");
        }
    }
}
