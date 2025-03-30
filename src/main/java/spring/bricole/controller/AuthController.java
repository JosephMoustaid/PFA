package spring.bricole.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.bricole.common.Gender;
import spring.bricole.dto.AuthResponse;
import spring.bricole.dto.EmployeeRegisterRequest;
import spring.bricole.dto.EmployerRegisterRequest;
import spring.bricole.model.Employee;
import spring.bricole.model.Employer;
import spring.bricole.service.AuthService;
import spring.bricole.service.EmployeeService;
import spring.bricole.service.EmployerService;
import spring.bricole.util.JwtUtil;
import spring.bricole.common.Role;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final EmployeeService employeeService;
    private final EmployerService employerService;

    public AuthController(AuthService authService, EmployeeService employeeService, EmployerService employerService) {
        this.authService = authService;
        this.employeeService = employeeService;
        this.employerService = employerService;
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

    @PostMapping("/employee/register")
    public ResponseEntity<AuthResponse> employeeRegister(
            @RequestBody EmployeeRegisterRequest request) {

        // Create and populate employee entity
        Employee employee = new Employee();
        employee.setFirstname(request.firstname());
        employee.setLastname(request.lastname());
        employee.setEmail(request.email());
        employee.setPassword(request.password()); // Let entity handle hashing
        employee.setAddress(request.address());
        employee.setGender(request.gender());
        employee.setPhoneNumberPrefix(request.phoneNumberPrefix());
        employee.setPhoneNumber(request.phoneNumber());
        employee.setProfilePicture(request.profilePicture());

        // Add collections
        request.skills().forEach(employee::addSkill);
        request.jobPreferences().forEach(employee::addJobPreference);
        request.availability().forEach(employee::updateAvailability);

        // Save employee
        Employee savedEmployee = employeeService.registerEmployee(employee);

        // Generate tokens
        Map<String, String> tokens = JwtUtil.generateTokens(savedEmployee.getId(), Role.EMPLOYEE);

        // Return response
        return ResponseEntity.ok(
                new AuthResponse(
                        tokens.get("access_token"),
                        tokens.get("refresh_token"),
                        savedEmployee.getId(),
                        savedEmployee.getFirstname() + " " + savedEmployee.getLastname(),
                        Role.EMPLOYEE.name()
                )
        );
    }

    @PostMapping("/employer/register")
    public ResponseEntity<AuthResponse> employerRegister(@RequestParam EmployerRegisterRequest request){
        // save employer to database using the service
        Employer emplyer = new Employer();
        emplyer.setFirstname(request.firstname());
        emplyer.setLastname(request.lastname());
        emplyer.setEmail(request.email());
        emplyer.setPassword(request.password());
        emplyer.setGender(request.gender());
        emplyer.setPhoneNumberPrefix(request.phoneNumberPrefix());
        emplyer.setPhoneNumber(request.phoneNumber());
        emplyer.setAddress(request.address());
        emplyer.setProfilePicture(request.profilePicture());

        // create tokens
        Map<String, String> tokens = JwtUtil.generateTokens(emplyer.getId(), Role.EMPLOYER);
        // return response
        return ResponseEntity.ok(
                new AuthResponse(
                        tokens.get("access_token"),
                        tokens.get("refresh_token"),
                        emplyer.getId(),
                        emplyer.getFirstname() + " " + emplyer.getLastname(),
                        Role.EMPLOYER.name()
                )
        );
    }
}