package spring.bricole.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring.bricole.dto.UserResponseDTO;
import spring.bricole.dto.UserUpdateDTO;
import spring.bricole.service.UserService;
import spring.bricole.util.JwtUtil;

import java.util.Set;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private static final Set<String> SUPPORTED_IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final UserService userService;

    public ProfileController(UserService userService) {
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

    // Get user profile (GET)
    @GetMapping("")
    public ResponseEntity<UserResponseDTO> getProfile(
            @RequestHeader("Authorization") String authorizationHeader) {

        int userId = extractUserIdFromToken(authorizationHeader);

        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    // Update profile (PUT)
    @PutMapping("")
    public ResponseEntity<UserResponseDTO> updateProfile(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody UserUpdateDTO updateDto) {

        int userId = extractUserIdFromToken(authorizationHeader);
        return ResponseEntity.ok(userService.updateProfile(userId, updateDto));
    }


    // Upload profile image (POST)
    @PostMapping("/image")
    public ResponseEntity<UserResponseDTO> uploadProfileImage(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("file") MultipartFile file) {
        int userId = extractUserIdFromToken(authorizationHeader);

        // verify the size
        long maxSize = 3 * 1024 * 1024; // 3MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("File too large. Max allowed size is 3MB");
        }

        // verify the file type: shoudl be image , and the extenssion shoudl be : jpeg, png or webp
        String contentType = file.getContentType();
        if (contentType == null || !SUPPORTED_IMAGE_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Unsupported image type: " + contentType);
        }
        return ResponseEntity.ok(userService.uploadProfileImage(userId, file));
    }

}
