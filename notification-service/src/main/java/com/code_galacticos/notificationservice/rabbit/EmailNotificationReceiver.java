package com.code_galacticos.notificationservice.rabbit;

import com.code_galacticos.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationReceiver {
    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveEmailNotification(EmailNotificationMessage message) {
        log.info("Received email notification message from RabbitMQ: to={}, subject='{}'",
                message.getTo(),
                message.getSubject());

        try {
            emailService.sendEmail(
                    message.getTo(),
                    message.getSubject(),
                    message.getText()
            );
            log.info("Successfully sent email to: {}", message.getTo());
        } catch (Exception e) {
            log.error("Failed to process email notification: {}", e.getMessage(), e);
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Email notification receiver is ready to process messages");
    }
}