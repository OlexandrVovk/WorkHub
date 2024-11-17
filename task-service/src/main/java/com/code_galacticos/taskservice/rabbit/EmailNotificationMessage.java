package com.code_galacticos.taskservice.rabbit;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailNotificationMessage {
    private String to;
    private String subject;
    private String text;
}
