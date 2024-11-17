package com.code_galacticos.notificationservice.rabbit;

import lombok.Data;

@Data
public class EmailNotificationMessage {
    private String to;
    private String subject;
    private String text;
}
