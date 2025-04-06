package spring.bricole.dto;

import java.time.LocalDateTime;


public record ChatMessageResponseDTO(
        int messageId,
        int conversationId,
        int senderId,
        String senderFirstName,
        String senderLastName,
        String content,
        LocalDateTime sentAt
) {}