package com.code_galacticos.taskservice.rabbit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationSender {
    private final RabbitTemplate rabbitTemplate;

    public void sendEmailNotification(EmailNotificationMessage message) {
        log.info("Sending email notification message to RabbitMQ: to={}, subject='{}'",
                message.getTo(),
                message.getSubject());

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY,
                    message
            );
            log.info("Successfully sent message to RabbitMQ");
        } catch (Exception e) {
            log.error("Failed to send message to RabbitMQ: {}", e.getMessage(), e);
            throw e;
        }
    }
}