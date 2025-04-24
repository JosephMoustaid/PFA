package spring.bricole.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.bricole.model.Notification;
import spring.bricole.service.NotificationService;
import spring.bricole.service.UserService;
import spring.bricole.util.JwtUtil;
import spring.bricole.dto.NotificationDTO;
import spring.bricole.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationController(NotificationService notificationService,
                                  UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    // Helper method to extract user ID from JWT token
    private int extractUserIdFromToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid or missing Authorization header");
        }
        String token = authorizationHeader.substring(7); // Remove "Bearer "
        JwtUtil.TokenValidationResult validation = JwtUtil.validateToken(token);
        return validation.userId(); // Returns the authenticated user's ID
    }

    // tested and validated
    // consult my notifications
    @GetMapping("/notifications")
    public ResponseEntity<?> getNotifications(
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            int userId = extractUserIdFromToken(authorizationHeader);
            User user = userService.getUserById(userId);

            List<NotificationDTO> notifications = new ArrayList<>();
            for(Notification notification : user.getReceivedNotifications()) {
                notifications.add(new NotificationDTO(
                        notification.getId(),
                        0,
                        notification.getMessage(),
                        notification.getCreatedAt()
                ));
            }

            return ResponseEntity.ok()
                    .body(Map.of(
                            "status", "success",
                            "message", "Notifications retrieved successfully",
                            "data", notifications
                    ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "status", "error",
                            "message", "Failed to retrieve notifications: " + e.getMessage()
                    ));
        }
    }

    // tested and validated
    // check a notification
    @GetMapping("/notifications/{notificationId}")
    public ResponseEntity<?> getNotificationById(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable int notificationId) {
        try {
            int userId = extractUserIdFromToken(authorizationHeader);
            User user = userService.getUserById(userId);

            for(Notification notification : user.getReceivedNotifications()) {
                if (notification.getId() == notificationId) {
                    NotificationDTO notificationDTO = new NotificationDTO(
                            notification.getId(),
                            0,
                            notification.getMessage(),
                            notification.getCreatedAt()
                    );
                    return ResponseEntity.ok()
                            .body(Map.of(
                                    "status", "success",
                                    "message", "Notification retrieved successfully",
                                    "data", notificationDTO
                            ));
                }
            }
            throw new RuntimeException("Notification not found");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "status", "error",
                            "message", "Failed to retrieve notification: " + e.getMessage()
                    ));
        }
    }

    // tested and validated
    // remove a notification
    @DeleteMapping("/notifications/{notificationId}")
    public ResponseEntity<?> removeNotification(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable int notificationId) {
        try {
            int userId = extractUserIdFromToken(authorizationHeader);
            User user = userService.getUserById(userId);

            for(Notification notification : user.getReceivedNotifications()) {
                if (notification.getId() == notificationId) {
                    userService.removeNotification(notification);
                    return ResponseEntity.ok()
                            .body(Map.of(
                                    "status", "success",
                                    "message", "Notification deleted successfully"
                            ));
                }
            }
            throw new RuntimeException("Notification not found");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "status", "error",
                            "message", "Failed to delete notification: " + e.getMessage()
                    ));
        }
    }
}