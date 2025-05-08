package spring.bricole.dto;

public record AdminAuthResponse(
        String access_token,
        String refresh_token,
        int userId,
        String email,
        String role
) {
}
