package spring.bricole.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.bricole.algorithms.RecommendationEngine;
import spring.bricole.dto.EmployeeResponseDTO;
import spring.bricole.dto.JobDTO;
import spring.bricole.model.Employee;
import spring.bricole.model.Employer;
import spring.bricole.model.Job;
import spring.bricole.service.EmployeeService;
import spring.bricole.service.EmployerService;
import spring.bricole.service.JobService;
import spring.bricole.util.JwtUtil;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rec")
public class RecommendationController {

    private final JobService jobService;
    private final EmployeeService employeeService;
    private final EmployerService employerService;

    public RecommendationController(EmployeeService employeeService, JobService jobService, EmployerService employerService) {
        this.employeeService = employeeService;
        this.jobService = jobService;
        this.employerService = employerService;
    }


    private int extractUserIdFromToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid or missing Authorization header");
        }
        String token = authorizationHeader.substring(7);
        JwtUtil.TokenValidationResult validation = JwtUtil.validateToken(token);
        return validation.userId();
    }

    // get jobs recommended for the connected employee and sorted by the rank
    @GetMapping("/jobs")
    public ResponseEntity<?> getRecommendedJobs(
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            int userId = extractUserIdFromToken(authorizationHeader);

            Employee employee = employeeService.getEmployeeById(userId);

            List<Job> allJobs = jobService.getAllJobs();
            Map<JobDTO, Double> jobsRecommendations = RecommendationEngine.rankJobsForEmployee(employee, allJobs);

            // Convert the map to a list of JSON-friendly objects
            List<Map<String, Object>> response = jobsRecommendations.entrySet().stream()
                    .map(entry -> Map.of(
                            "job", entry.getKey(),
                            "rank", entry.getValue()
                    ))
                    .toList();

            return ResponseEntity.ok()
                    .body(Map.of(
                            "status", "success",
                            "message", "Successfully  ",
                            "data", response
                    ));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "status", "error",
                            "message", "Failed to retriece recommendations" + e.getMessage()
                    ));
        }
    }

    // get recommended employees for a job
    @GetMapping("/job/{id}/employees")
    public ResponseEntity<?> getRecommendedEmployees(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable int id) {
        try {
            int userId = extractUserIdFromToken(authorizationHeader);

            Employer employer = employerService.getEmployerById(userId);

            List<Job> allJobs = jobService.getAllJobs();
            Map<EmployeeResponseDTO, Double> employeessRecommendations = RecommendationEngine.rankEmployeesForEmployerJob(employeeService.getAllEmployees(), jobService.getJobById(id), allJobs);

            // Convert the map to a list of JSON-friendly objects
            List<Map<String, Object>> response = employeessRecommendations.entrySet().stream()
                    .map(entry -> Map.of(
                            "employee", entry.getKey(),
                            "rank", entry.getValue()
                    ))
                    .toList();

            return ResponseEntity.ok()
                    .body(Map.of(
                            "status", "success",
                            "message", "Successfully  ",
                            "data", employeessRecommendations
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
