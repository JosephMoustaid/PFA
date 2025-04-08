package spring.bricole.dto;

import java.time.LocalDateTime;

public record NotificationDTO(
        int notificationId,
        int senderId,
        String message,
        LocalDateTime sentAt
) {
}
