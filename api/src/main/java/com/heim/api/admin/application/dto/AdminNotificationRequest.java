package com.heim.api.admin.application.dto;

import lombok.Data;

@Data
public class AdminNotificationRequest {
    private String title;
    private String body;
    private String target; // ALL_USERS, ALL_DRIVERS, SPECIFIC_USER
    private Long userId;   // solo requerido si target = SPECIFIC_USER
}
