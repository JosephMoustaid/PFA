package spring.bricole.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.bricole.algorithms.RecommendationEngine;
import spring.bricole.common.ApplicationState;
import spring.bricole.dto.EmployeeDTO;
import spring.bricole.dto.JobDTO;
import spring.bricole.model.Employee;
import spring.bricole.model.Job;
import spring.bricole.service.EmployeeService;
import spring.bricole.service.JobService;
import spring.bricole.util.JwtUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    private final JobService jobService;
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService, JobService jobService) {
        this.employeeService = employeeService;
        this.jobService = jobService;
    }

    private int extractUserIdFromToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid or missing Authorization header");
        }
        String token = authorizationHeader.substring(7);
        JwtUtil.TokenValidationResult validation = JwtUtil.validateToken(token);
        return validation.userId();
    }

    // tested and validated
    @GetMapping("/details")
    public ResponseEntity<?> getEmployeeDetails(
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            int userId = extractUserIdFromToken(authorizationHeader);
            Employee employee = employeeService.getEmployeeById(userId);

            EmployeeDTO response = new EmployeeDTO(
                    employee.getFirstname(),
                    employee.getLastname(),
                    employee.getPhoneNumberPrefix(),
                    employee.getPhoneNumber(),
                    employee.getSkills(),
                    employee.getProfilePicture(),
                    employee.getAvailabilityDaysOfWeek(),
                    employee.getJobPreferences(),
                    employee.getReviews()
            );

            return ResponseEntity.ok()
                    .body(Map.of(
                            "status", "success",
                            "message", "Employee details retrieved successfully",
                            "data", response
                    ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "status", "error",
                            "message", "Failed to retrieve employee details: " + e.getMessage()
                    ));
        }
    }
    // tested and validated
    @PutMapping("/update")
    public ResponseEntity<?> updateEmployee(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody EmployeeDTO updatedEmployee) {
        try {
            int userId = extractUserIdFromToken(authorizationHeader);
            Employee employee = employeeService.getEmployeeById(userId);

            employee.setFirstname(updatedEmployee.firstname());
            employee.setLastname(updatedEmployee.lastname());
            employee.setPhoneNumberPrefix(updatedEmployee.phoneNumberPrefix());
            employee.setPhoneNumber(updatedEmployee.phoneNumber());
            employee.setSkills(updatedEmployee.skills());
            employee.setAvailabilityDaysOfWeek(updatedEmployee.availabilityMap());
            employee.setJobPreferences(updatedEmployee.jobPreferences());

            employeeService.saveEmployee(employee);

            return ResponseEntity.ok()
                    .body(Map.of(
                            "status", "success",
                            "message", "Employee profile updated successfully",
                            "data", updatedEmployee
                    ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "status", "error",
                            "message", "Failed to update employee profile: " + e.getMessage()
                    ));
        }
    }

    // tested and validated
    @GetMapping("/applications")
    public ResponseEntity<?> getApplications(
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            int userId = extractUserIdFromToken(authorizationHeader);
            Map<Job, ApplicationState> applications = employeeService.getAppliedJobs(userId);

            List<Map<String, Object>> response = applications.entrySet().stream()
                    .map(entry -> Map.of(
                            "job", new JobDTO(entry.getKey()),
                            "state", entry.getValue()
                    ))
                    .toList();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "status", "error",
                            "message", "Failed to retrieve applications: " + e.getMessage()
                    ));
        }
    }


    // tested and validated
    @GetMapping("/application/{applicationId}")
    public ResponseEntity<?> getApplication(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable int applicationId) {
        try {
            int userId = extractUserIdFromToken(authorizationHeader);
            Map<Job, ApplicationState> applications = employeeService.getAppliedJobs(userId);

            for (Map.Entry<Job, ApplicationState> entry : applications.entrySet()) {
                if (entry.getKey().getId() == applicationId) {
                    return ResponseEntity.ok()
                            .body(Map.of(
                                    "status", "success",
                                    "message", "Application retrieved successfully",
                                    "data", entry.getKey()
                            ));
                }
            }
            throw new RuntimeException("Application not found");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "status", "error",
                            "message", "Failed to retrieve application: " + e.getMessage()
                    ));
        }
    }

    // tested and validated
    @PostMapping("/apply/job/{jobId}")
    public ResponseEntity<?> applyForJob(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable int jobId) {
        try {
            int userId = extractUserIdFromToken(authorizationHeader);
            Job job = jobService.getJobById(jobId);


            if(job.getApplicants().containsKey(userId)) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "status", "error",
                                "message", "Already applied for this job"
                        ));
            }
            job.getApplicants().put(userId, ApplicationState.PENDING);
            jobService.updateJob(jobId, job);

            return ResponseEntity.ok()
                    .body(Map.of(
                            "status", "success",
                            "message", "Successfully applied for job",
                            "data", job
                    ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "status", "error",
                            "message", "Failed to apply for job: " + e.getMessage()
                    ));
        }
    }

    // get jobs recommended for the connected employee and sorted by the rank
    @GetMapping("/recommended/jobs")
    public ResponseEntity<?> getRecommendedJobs(
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            int userId = extractUserIdFromToken(authorizationHeader);

            Employee employee = employeeService.getEmployeeById(userId);

            List<Job> allJobs = jobService.getAllJobs();
            Map<Job, Double> jobsRecommendations = RecommendationEngine.rankJobsForEmployee(employee, allJobs);
            return ResponseEntity.ok()
                    .body(Map.of(
                            "status", "success",
                            "message", "Successfully  ",
                            "data", jobsRecommendations
                    ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "status", "error",
                            "message", "Failed to retriece recommendations" + e.getMessage()
                    ));
        }
    }
}