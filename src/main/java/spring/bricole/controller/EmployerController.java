package spring.bricole.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.bricole.common.ApplicationState;
import spring.bricole.common.JobStatus;
import spring.bricole.dto.*;
import spring.bricole.model.Employee;
import spring.bricole.model.Job;
import spring.bricole.model.Review;
import spring.bricole.service.EmployeeService;
import spring.bricole.service.EmployerService;
import spring.bricole.service.JobService;
import spring.bricole.service.ReviewService;
import spring.bricole.util.JwtUtil;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/employer")
public class EmployerController {

    private final JobService jobService;
    private final EmployerService empployerService;
    private final EmployeeService employeeService;
    private final EmployerService employerService;

    public EmployerController(EmployerService empployerService, JobService jobService,
                              EmployeeService employeeService, EmployerService employerService) {
        this.empployerService = empployerService;
        this.jobService = jobService;
        //
        this.employeeService = employeeService;
        this.employerService = employerService;
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

    // create a new job
    @PostMapping("/job/create")
    public ResponseEntity<Job> createJob(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody JobDTO jobDto) {
        int userId = extractUserIdFromToken(authorizationHeader);


        Job job = new Job();
        job.setTitle(jobDto.getTitle());
        job.setDescription(jobDto.getDescription());
        job.setLocation(jobDto.getLocation());
        job.setSalary(jobDto.getSalary());
        job.setCategory(jobDto.getCategory());
        job.setEmployer(empployerService.getEmployerById(userId) );
        job.setMissions(jobDto.getMissions());
        job.setMedia(jobDto.getMedia());
        job.setCreatedAt(LocalDateTime.now());
        job.setStatus(JobStatus.OPEN);
        job.setLocation(jobDto.getLocation());




        return ResponseEntity.ok(jobService.createJob(job) );
    }


    // tested and validated
    @GetMapping("/job/{id}/applicants")
    public ResponseEntity<Map<EmployeeResponseDTO, ApplicationState>> getJobApplicants(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable int id) {
        int userId = extractUserIdFromToken(authorizationHeader);

        Job job = jobService.getJobById(id);

        Map<Integer, ApplicationState> jobApplicants = job.getApplicants();

        Map<EmployeeResponseDTO, ApplicationState> applicants = new HashMap<>();

        for (Map.Entry<Integer, ApplicationState> entry : jobApplicants.entrySet()) {
            int applicantId = entry.getKey();
            ApplicationState state = entry.getValue();
            Employee employee = employeeService.getEmployeeById(applicantId);
            EmployeeResponseDTO employeeDTO = new EmployeeResponseDTO(
                    applicantId,
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
            applicants.put(employeeDTO, state);
        }
        return ResponseEntity.ok(applicants);
    }

    // tested and validated
    @GetMapping("/job/{id}/applications/{employeeId}")
    public ResponseEntity<JobApplicationEmployeeAndStateResponseDTO> getJobApplication(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable int id,
            @PathVariable int employeeId) {
        int userId = extractUserIdFromToken(authorizationHeader);

        // Retrieve the job by ID
        Job job = jobService.getJobById(id);
        if (job == null) {
            System.out.println("Job not found with ID: " + id);
            return ResponseEntity.notFound().build();
        }

        // Retrieve the applicants for the job
        Map<Integer, ApplicationState> jobApplicants = job.getApplicants();
        if (jobApplicants == null || jobApplicants.isEmpty()) {
            System.out.println("No applicants found for job ID: " + id);
            return ResponseEntity.notFound().build();
        }

        // Find the employee by full name
        Employee emp = employeeService.getEmployeeById(employeeId);

        ApplicationState applicationState = jobApplicants.get(employeeId);

        if (emp == null ) {
            System.out.println("Employee not found with id : " + employeeId);
            return ResponseEntity.notFound().build();
        }


        // Create the EmployeeDTO
        EmployeeResponseDTO employeeDTO = new EmployeeResponseDTO(
                emp.getId(),
                emp.getFirstname(),
                emp.getLastname(),
                emp.getPhoneNumberPrefix(),
                emp.getPhoneNumber(),
                emp.getSkills(),
                emp.getProfilePicture(),
                emp.getAvailabilityDaysOfWeek(),
                emp.getJobPreferences(),
                emp.getReviews()
        );

        // Create the response DTO
        JobApplicationEmployeeAndStateResponseDTO response = new JobApplicationEmployeeAndStateResponseDTO( employeeDTO , applicationState);

        return ResponseEntity.ok(response);
    }

    // tested and validated
    @GetMapping("/job/offers")
    public ResponseEntity<List<Job>> getJobOffers(
            @RequestHeader("Authorization") String authorizationHeader) {
        int userId = extractUserIdFromToken(authorizationHeader);

        return ResponseEntity.ok(jobService.findByEmployerId(userId));
    }




    // tested and validated
    @PostMapping("/employee/{id}/review")
    public ResponseEntity<String> addReview(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable int id,
            @RequestBody ReviewRequestDTO review) {
        int userId = extractUserIdFromToken(authorizationHeader);

        Employee employee = employeeService.getEmployeeById(id);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }
        if (review.rating() < 1 || review.rating() > 5) {
            return ResponseEntity.badRequest().body("Rating must be between 1 and 5");
        }
       try{
           employee.addReview(employerService.getEmployerById(userId), review.review(), review.rating());
       }catch (InvalidPropertiesFormatException e){
              return ResponseEntity.badRequest().body("Invalid review format");
       }
        employeeService.saveEmployee(employee);
        return ResponseEntity.ok("Review added successfully");
    }

}
