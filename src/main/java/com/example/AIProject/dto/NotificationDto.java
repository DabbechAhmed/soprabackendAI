package com.example.AIProject.dto;

import com.example.AIProject.enums.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDto {
    private Long id;
    private String title;
    private String message;
    private NotificationType type;
    private Boolean isRead;
    private LocalDateTime createdAt;

    // Informations de l'application liée (si présente)
    private Long applicationId;
    private String positionTitle;
    private String applicantName; // Pour les HR
}
