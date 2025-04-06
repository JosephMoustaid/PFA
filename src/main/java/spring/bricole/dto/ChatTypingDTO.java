package spring.bricole.dto;


public record ChatTypingDTO(
        int userId,
        String firstName,
        String lastName,
        boolean isTyping
) {}
