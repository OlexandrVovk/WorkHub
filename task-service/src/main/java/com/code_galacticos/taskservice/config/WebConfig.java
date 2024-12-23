package com.code_galacticos.taskservice.config;

import com.code_galacticos.taskservice.firebase.FirebaseAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final FirebaseAuthInterceptor firebaseAuthInterceptor;
    private final CurrentUserArgumentResolver currentUserArgumentResolver;

    @Autowired
    public WebConfig(FirebaseAuthInterceptor firebaseAuthInterceptor, CurrentUserArgumentResolver currentUserArgumentResolver) {
        this.firebaseAuthInterceptor = firebaseAuthInterceptor;
        this.currentUserArgumentResolver = currentUserArgumentResolver;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(firebaseAuthInterceptor)
                .addPathPatterns("/api/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserArgumentResolver);
    }
}