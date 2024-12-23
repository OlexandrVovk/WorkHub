package com.code_galacticos.notificationservice.controller;

import com.code_galacticos.notificationservice.model.dto.EmailRequest;
import com.code_galacticos.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @GetMapping
    public ResponseEntity<String> sayHi() {
        return ResponseEntity.ok("Email Service is working!");
    }

    @PostMapping("/email")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request) {
        emailService.sendEmail(
                request.getTo(),
                request.getSubject(),
                request.getText()
        );
        return ResponseEntity.ok("Email sent successfully");
    }
}
