package com.example.AIProject.services.notification;

import com.example.AIProject.dto.NotificationDto;
import com.example.AIProject.entities.Application;

import java.util.List;

public interface INotificationService {
    void notifyApplicationSubmitted(Application application);

    void notifyApplicationAccepted(Application application);

    void notifyApplicationRejected(Application application);
    List<NotificationDto> getUserNotifications(Long userId);

    void markAsRead(Long notificationId);

}
