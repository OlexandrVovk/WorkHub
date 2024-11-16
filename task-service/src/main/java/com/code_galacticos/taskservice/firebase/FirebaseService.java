package com.code_galacticos.taskservice.firebase;
import com.code_galacticos.taskservice.exception.TokenExpiredException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FirebaseService {

    public String verifyToken(String token) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            return decodedToken.getUid();
        } catch (FirebaseAuthException e) {
            log.error("Firebase token verification failed", e);
            if (e.getMessage().contains("expired")) {
                throw new TokenExpiredException("Token has expired. Please refresh your token.");
            }
            throw new RuntimeException("Invalid token");
        }
    }
}
