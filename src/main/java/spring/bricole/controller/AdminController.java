package spring.bricole.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.bricole.common.AccountStatus;
import spring.bricole.common.ApplicationState;
import spring.bricole.common.Role;
import spring.bricole.dto.JobApplicantDTO;
import spring.bricole.dto.JobApplicationDTO;
import spring.bricole.dto.JobDTO;
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
    public ResponseEntity<List<JobApplicantDTO>> getApplicationsByJobId(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer id) {
        verifyAdminToken(authorizationHeader);
        Map<Integer, ApplicationState> applications = jobService.getApplicationsByJobId(id);

        // Convert Map<Integer, ApplicationState> to List<JobApplicationDTO>
        List<JobApplicantDTO> applicantDTOs = applications.entrySet().stream()
                .map(entry -> new JobApplicantDTO(employeeService.getEmployeeById(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(applicantDTOs);
    }

    @GetMapping("/applications/employee/{id}")
    public ResponseEntity<List<JobApplicationDTO>> getApplicationsByEmployeeId(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer id) {
        verifyAdminToken(authorizationHeader);
        Map<Job, ApplicationState> applications = jobService.getApplicationsByEmployeeId(id);

        List<JobApplicationDTO> applicationDTOs = applications.entrySet().stream()
                .map(entry -> new JobApplicationDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(applicationDTOs);
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

    // == Updates ==
    // Change the status of a user's account
    // @PostMapping("/user/{id}/updateStatus/{newStatus")

    @PostMapping("/user/{id}/updateStatus/{newStatus}")
    public ResponseEntity<String> updateUserStatus(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer id ,
            @PathVariable String newStatus ){
        verifyAdminToken(authorizationHeader);

        if(newStatus == null || newStatus.isEmpty()){
            return ResponseEntity.badRequest().body("Invalid status");
        }

        User user = userService.getUserById(id);
        if(user == null){
            return ResponseEntity.badRequest().body("User not found");
        }
        if( newStatus.equals(user.getStatus().toString()) )
            return ResponseEntity.badRequest().body("User already has this status");

        if(newStatus.equals("INACTIVE") || newStatus.equals("ACTIVE") || newStatus.equals("SUSPENDED") || newStatus.equals("BANNED")){
            userService.updateUserAccountStatus(id, AccountStatus.valueOf(newStatus) );

            userService.addNotification(id, "Your account status was changed to " + newStatus + " by the admin");

            return ResponseEntity.ok("User status updated successfully");
        }
        else{
            return ResponseEntity.badRequest().body("Invalid status");
        }
    }

    @PostMapping("/job/{id}/updateJob")
    public ResponseEntity<String> updateJob(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer id,
            @RequestBody JobDTO jobDetails) {
        verifyAdminToken(authorizationHeader);
        Job job = jobService.getJobById(id);
        if (job == null) {
            return ResponseEntity.badRequest().body("Job not found");
        }

        // Convert JobDTO to Job entity
        job.setTitle(jobDetails.getTitle());
        job.setDescription(jobDetails.getDescription());
        job.setCategory(jobDetails.getCategory());
        job.setStatus(jobDetails.getStatus());
        job.setLocation(jobDetails.getLocation());
        job.setSalary(jobDetails.getSalary());
        job.setMedia(jobDetails.getMedia());
        job.setMissions(jobDetails.getMissions());
        job.setCreatedAt(jobDetails.getCreatedAt());

        jobService.updateJob(id, job);
        return ResponseEntity.ok("Job updated successfully");
    }
}
