package com.code_galacticos.taskservice.firebase;

import com.code_galacticos.taskservice.exception.TokenExpiredException;
import com.code_galacticos.taskservice.model.entity.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Log4j2
public class FirebaseAuthInterceptor implements HandlerInterceptor {

    private final FirebaseService firebaseService;
    private static final int TOKEN_EXPIRED_STATUS = 498;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String token = authHeader.substring(7);
        try {
            UserEntity user = firebaseService.verifyToken(token);
            request.setAttribute("user-id", user.getId());
            return true;
        } catch (TokenExpiredException e) {
            response.setStatus(TOKEN_EXPIRED_STATUS);
            response.setContentType("application/json");
            try {
                response.getWriter().write("{\"message\": \"" + e.getMessage() + "\"}");
            } catch (Exception ex) {
                log.error("Error writing response", ex);
            }
            return false;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }
}