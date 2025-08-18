package com.example.AIProject.controllers;

import com.example.AIProject.dto.NotificationDto;
import com.example.AIProject.responses.ApiResponse;
import com.example.AIProject.services.notification.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/notifications")
public class NotificationController {
    private final INotificationService notificationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getUserNotifications(@PathVariable Long userId) {
        List<NotificationDto> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(new ApiResponse("Notifications retrieved successfully", notifications));
    }

    @PostMapping("/mark-as-read/{notificationId}")
    public ResponseEntity<ApiResponse> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(new ApiResponse("Notification marked as read", null));
    }

}
