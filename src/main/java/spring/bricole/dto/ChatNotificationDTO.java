package spring.bricole.dto;

import java.time.LocalDateTime;

// ChatNotificationDTO.java

public record ChatNotificationDTO(
        int userId,
        String content,
        LocalDateTime timestamp
) {}
