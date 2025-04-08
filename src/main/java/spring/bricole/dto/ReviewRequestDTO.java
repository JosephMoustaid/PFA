package spring.bricole.dto;

public record ReviewRequestDTO(
        String review,
        int rating,
        int employeeId
) {
}
