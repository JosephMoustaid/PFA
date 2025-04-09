package spring.bricole.dto;

public record ResetPasswordRequestDTO(
        String email,
        String oldPassword,
        String newPassword
) {
}
