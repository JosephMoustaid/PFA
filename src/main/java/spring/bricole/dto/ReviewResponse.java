package spring.bricole.dto;

import java.time.LocalDateTime;

public record ReviewResponse(
        int id,
        String reviewerName,
        String content,
        int rating,
        LocalDateTime createdAt,
        int reviewedEmployeeId,
        int reviewerId
) {
}
