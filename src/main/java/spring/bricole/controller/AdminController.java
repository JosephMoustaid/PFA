package spring.bricole.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.bricole.common.Role;
import spring.bricole.model.*;
import spring.bricole.service.*;
import spring.bricole.util.JwtUtil;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private final EmployeeService employeeService;
    private final EmployerService employerService;
    private final JobService jobService;
    private final UserService userService;

    public AdminController(AdminService adminService, EmployeeService employeeService,
                           EmployerService employerService, JobService jobService,
                           UserService userService) {
        this.adminService = adminService;
        this.employeeService = employeeService;
        this.employerService = employerService;
        this.jobService = jobService;
        this.userService = userService;
    }

    // Add this method to verify admin tokens
    private void verifyAdminToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid or missing Authorization header");
        }

        String token = authorizationHeader.substring(7);
        JwtUtil.TokenValidationResult validation = JwtUtil.validateToken(token);

        if (validation.role() != Role.ADMIN) {
            throw new RuntimeException("Access denied. Admin role required");
        }
    }

    // == Fetchces ==
    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAllEmployees(
            @RequestHeader("Authorization") String authorizationHeader) {
        verifyAdminToken(authorizationHeader);
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity<Employee> getEmployeeById(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer id) {
        verifyAdminToken(authorizationHeader);
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/employers")
    public ResponseEntity<List<Employer>> getAllEmployers(
            @RequestHeader("Authorization") String authorizationHeader) {
        verifyAdminToken(authorizationHeader);
        List<Employer> employers = adminService.getAllEmployers();
        return ResponseEntity.ok(employers);
    }

    @GetMapping("/employer/{id}")
    public ResponseEntity<Employer> getEmployerById(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer id) {
        verifyAdminToken(authorizationHeader);
        Employer employer = employerService.getEmployerById(id);
        return ResponseEntity.ok(employer);
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<Job>> getAllJobs(
            @RequestHeader("Authorization") String authorizationHeader) {
        verifyAdminToken(authorizationHeader);
        List<Job> jobs = adminService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/job/{id}")
    public ResponseEntity<Job> getJobById(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer id) {
        verifyAdminToken(authorizationHeader);
        Job job = jobService.getJobById(id);
        return ResponseEntity.ok(job);
    }

    // == Deletes ==
    @DeleteMapping("/employee/{id}")
    public ResponseEntity<String> deleteEmployeeById(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer id) {
        verifyAdminToken(authorizationHeader);
        boolean deleted = employeeService.deleteEmployeeById(id);
        if (deleted) {
            return ResponseEntity.ok("Employee deleted successfully");
        } else {
            return ResponseEntity.badRequest().body("Employee not found");
        }
    }

    @DeleteMapping("/employer/{id}")
    public ResponseEntity<String> deleteEmployerById(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer id) {
        verifyAdminToken(authorizationHeader);
        boolean deleted = employerService.deleteEmployerById(id);
        if (deleted) {
            return ResponseEntity.ok("Employer deleted successfully");
        } else {
            return ResponseEntity.badRequest().body("Employer not found");
        }
    }

    @DeleteMapping("/job/{id}")
    public ResponseEntity<String> deleteJobById(
            @RequestParam("Authorization") String authorizationHeader,
            @PathVariable Integer id) {
        verifyAdminToken(authorizationHeader);
        boolean deleted = jobService.deleteJobById(id);
        if(deleted) {
            return ResponseEntity.ok("Job deleted successfully");
        } else {
            return ResponseEntity.badRequest().body("Job not found");
        }
    }
}
