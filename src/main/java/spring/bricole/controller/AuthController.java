package spring.bricole.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.bricole.dto.AuthResponse;
import spring.bricole.service.AuthService;
import spring.bricole.util.JwtUtil;
import spring.bricole.common.Role;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/employer/login")
    public ResponseEntity<AuthResponse> employerLogin(
            @RequestParam String email,
            @RequestParam String password) {

        var employer = authService.authenticateEmployer(email, password);
        Map<String, String> tokens = JwtUtil.generateTokens(employer.getId(), Role.EMPLOYER);

        return ResponseEntity.ok(
                new AuthResponse(
                        tokens.get("access_token"),
                        tokens.get("refresh_token"),
                        employer.getId(),
                        employer.getFirstname() + " " + employer.getLastname(),
                        Role.EMPLOYER.name()
                )
        );
    }

    @PostMapping("/employee/login")
    public ResponseEntity<AuthResponse> employeeLogin(
            @RequestParam String email,
            @RequestParam String password) {

        var employee = authService.authenticateEmployee(email, password);
        Map<String, String> tokens = JwtUtil.generateTokens(employee.getId(), Role.EMPLOYEE);

        return ResponseEntity.ok(
                new AuthResponse(
                        tokens.get("access_token"),
                        tokens.get("refresh_token"),
                        employee.getId(),
                        employee.getFirstname() + " " + employee.getLastname(),
                        Role.EMPLOYEE.name()
                )
        );
    }

    @PostMapping("/admin/login")
    public ResponseEntity<AuthResponse> adminLogin(
            @RequestParam String email,
            @RequestParam String password) {

        var admin = authService.authenticateAdmin(email, password);
        Map<String, String> tokens = JwtUtil.generateTokens(admin.getId(), Role.ADMIN);

        return ResponseEntity.ok(
                new AuthResponse(
                        tokens.get("access_token"),
                        tokens.get("refresh_token"),
                        admin.getId(),
                        admin.getId() + " " + admin.getEmail(),
                        Role.ADMIN.name()
                )
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestParam String refreshToken) {
        JwtUtil.TokenValidationResult validation = JwtUtil.validateToken(refreshToken);
        Map<String, String> newTokens = JwtUtil.generateTokens(validation.userId(), validation.role());

        return ResponseEntity.ok(
                new AuthResponse(
                        newTokens.get("access_token"),
                        newTokens.get("refresh_token"),
                        validation.userId(),
                        "", // Can be fetched from DB if needed
                        validation.role().name()
                )
        );
    }
}