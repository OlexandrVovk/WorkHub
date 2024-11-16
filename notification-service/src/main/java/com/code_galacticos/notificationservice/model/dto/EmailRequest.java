package com.code_galacticos.notificationservice.model.dto;

import lombok.Data;

@Data
public class EmailRequest {
    private String to;
    private String subject;
    private String text;
}