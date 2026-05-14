package com.heim.api.notification.application.dto;


import com.heim.api.notification.domain.model.NotificationTarget;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private String title;
    private String body;
    private Long targetId;; // ID del conductor o usuario
    private NotificationTarget target;
}
