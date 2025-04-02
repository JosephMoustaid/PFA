package spring.bricole.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.bricole.common.ApplicationState;
import spring.bricole.common.Role;
import spring.bricole.model.*;
import spring.bricole.service.*;
import spring.bricole.util.JwtUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /*
       @ElementCollection
    @CollectionTable(name = "job_applicants", joinColumns = @JoinColumn(name = "job_id"))
    @MapKeyColumn(name = "employee_id") // Store employee ID instead of Employee entity
    @Column(name = "application_state")
    @Enumerated(EnumType.STRING)
    private Map<Integer, ApplicationState> applicants = new HashMap<>();

     */
    @GetMapping("/applications/job/{id}")
    public ResponseEntity<List<Map<String, Object>>> getApplicationsByJobId(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer id) {
        verifyAdminToken(authorizationHeader);
        List<Object[]> results = jobService.getApplicationsByJobId(id);

        List<Map<String, Object>> applications = results.stream()
                .map(result -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    // Assuming result[0] is Employee entity and result[1] is application state
                    Employee employee = (Employee) result[0];
                    ApplicationState state = (ApplicationState) result[1];

                    map.put("employeeId", employee.getId());
                    map.put("employeeName", employee.getFirstname() + " " + employee.getLastname());
                    // Add other employee fields as needed
                    map.put("applicationState", state);

                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(applications);
    }

    @GetMapping("/applications/employee/{id}")
    public ResponseEntity<List<Map<String, Object>>> getApplicationsByEmployeeId(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer id) {
        verifyAdminToken(authorizationHeader);
        List<Object[]> results = jobService.getApplicationsByEmployeeId(id);

        List<Map<String, Object>> applications = results.stream()
                .map(result -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    // Assuming result[0] is Job entity and result[1] is application state
                    Job job = (Job) result[0];
                    ApplicationState state = (ApplicationState) result[1];

                    map.put("jobId", job.getId());
                    map.put("jobTitle", job.getTitle());
                    // Add other job fields as needed
                    map.put("applicationState", state);

                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(applications);
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
