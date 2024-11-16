package com.code_galacticos.notificationservice.controller;

import com.code_galacticos.notificationservice.model.dto.EmailRequest;
import com.code_galacticos.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
public class EmailController {

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

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
