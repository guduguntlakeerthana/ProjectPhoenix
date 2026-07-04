package com.devflowai.controller;

import com.devflowai.dto.response.NotificationResponse;
import com.devflowai.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:4300")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<NotificationResponse> getMyNotifications(Authentication authentication) {
        return notificationService.getMyNotifications(authentication.getName());
    }

    @PutMapping("/{id}/read")
    public NotificationResponse markAsRead(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return notificationService.markAsRead(id, authentication.getName());
    }

    @PutMapping("/read-all")
    public String markAllAsRead(Authentication authentication) {
        notificationService.markAllAsRead(authentication.getName());
        return "All notifications marked as read";
    }

    @DeleteMapping("/{id}")
    public String deleteNotification(
            @PathVariable Long id,
            Authentication authentication
    ) {
        notificationService.deleteNotification(id, authentication.getName());
        return "Notification deleted successfully";
    }
}
