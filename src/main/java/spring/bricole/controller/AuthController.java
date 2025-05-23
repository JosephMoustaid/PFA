package spring.bricole.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.bricole.common.Gender;
import spring.bricole.dto.*;
import spring.bricole.model.Employee;
import spring.bricole.model.Employer;
import spring.bricole.model.User;
import spring.bricole.service.*;
import spring.bricole.util.JwtUtil;
import spring.bricole.common.Role;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final EmployeeService employeeService;
    private final EmployerService employerService;
    private final UserService userService;
    private final AdminService adminService;

    public AuthController(AuthService authService, EmployeeService employeeService, EmployerService employerService, UserService userService, AdminService adminService) {
        this.authService = authService;
        this.employeeService = employeeService;
        this.employerService = employerService;
        this.userService = userService;
        this.adminService = adminService;
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

    // common login route for both employee and employer
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestParam String email,
            @RequestParam String password) {

        int id = authService.authentificateUser(email, password);
        String role = authService.getUserRole(id);

        Map<String, String> tokens = JwtUtil.generateTokens(id, Role.valueOf(role));

        return ResponseEntity.ok(
                new AuthResponse(
                        tokens.get("access_token"),
                        tokens.get("refresh_token"),
                        id,
                        authService.getUserFullname(id),
                        role
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
    public ResponseEntity<AdminAuthResponse> adminLogin(
            @RequestParam String email,
            @RequestParam String password) {

        var admin = authService.authenticateAdmin(email, password);
        Map<String, String> tokens = JwtUtil.generateTokens(admin.getId(), Role.ADMIN); // ✅ Correct line

        return ResponseEntity.ok(
                new AdminAuthResponse(
                        tokens.get("access_token"),
                        tokens.get("refresh_token"),
                        admin.getId(),
                        admin.getEmail(),
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
    public ResponseEntity<AuthResponse> employerRegister(@RequestBody EmployerRegisterRequest request){
        Employer employer = new Employer();
        employer.setFirstname(request.firstname());
        employer.setLastname(request.lastname());
        employer.setEmail(request.email());
        employer.setPassword(request.password()); // hash inside setter if applicable
        employer.setGender(request.gender());
        employer.setPhoneNumberPrefix(request.phoneNumberPrefix());
        employer.setPhoneNumber(request.phoneNumber());
        employer.setAddress(request.address());

        // Save the employer to database
        Employer savedEmployer = employerService.registerEmployer(employer);

        // Generate tokens using the actual saved ID
        Map<String, String> tokens = JwtUtil.generateTokens(savedEmployer.getId(), Role.EMPLOYER);

        return ResponseEntity.ok(
                new AuthResponse(
                        tokens.get("access_token"),
                        tokens.get("refresh_token"),
                        savedEmployer.getId(),
                        savedEmployer.getFirstname() + " " + savedEmployer.getLastname(),
                        Role.EMPLOYER.name()
                )
        );
    }


    // reset passowrd
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequestDTO request) {
        String email = request.email();
        String oldPassword = request.oldPassword();
        String newPassword = request.newPassword();

        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("Invalid credentials.");
        }

        if (!Bcrypt.checkPassword(oldPassword, user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid credentials.");
        }

        if (!isValidPassword(newPassword)) {
            return ResponseEntity.badRequest().body("New password must be at least 8 characters long and include uppercase letters, numbers, and special characters.");
        }

        userService.updateUserPassword(user.getId() , newPassword);

        return ResponseEntity.ok("Password updated successfully.");
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        return password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }

    // update admin passowrd
    @PutMapping("/admin/reset-password")
    public ResponseEntity<String> resetAdminPassword(@RequestBody ResetPasswordRequestDTO request) {
        String email = request.email();
        String oldPassword = request.oldPassword();
        String newPassword = request.newPassword();


        if (!isValidPassword(newPassword)) {
            return ResponseEntity.badRequest().body("New password must be at least 8 characters long and include uppercase letters, numbers, and special characters.");
        }

        authService.updateAdminPassword(email , oldPassword, newPassword);
        // the updateUserPassword method already checks the old password
        return ResponseEntity.ok("Password updated successfully." );
    }
}