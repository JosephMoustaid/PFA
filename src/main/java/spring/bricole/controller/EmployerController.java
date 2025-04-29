package spring.bricole.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring.bricole.algorithms.RecommendationEngine;
import spring.bricole.common.ApplicationState;
import spring.bricole.common.JobStatus;
import spring.bricole.dto.*;
import spring.bricole.model.Employee;
import spring.bricole.model.Employer;
import spring.bricole.model.Job;
import spring.bricole.model.Review;
import spring.bricole.service.*;
import spring.bricole.util.JwtUtil;
import spring.bricole.util.ObjectMapper;
import spring.bricole.dto.ApplicantDTO;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/employer")
public class EmployerController {

    private final JobService jobService;
    private final EmployerService empployerService;
    private final EmployeeService employeeService;
    private final EmployerService employerService;
    private final UserService userService;
    private final ReviewService reviewService;

    private static final Set<String> SUPPORTED_IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp");


    public EmployerController(EmployerService empployerService, JobService jobService,
                              EmployeeService employeeService, EmployerService employerService,
                              UserService userService, ReviewService reviewService) {
        this.empployerService = empployerService;
        this.jobService = jobService;
        //
        this.employeeService = employeeService;
        this.employerService = employerService;
        this.userService = userService;
        this.reviewService = reviewService;
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
    @PostMapping("/job/create")
    public ResponseEntity<Job> createJob(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestPart("job") JobDTO jobDto,
            @RequestPart(name = "media", required = false) MultipartFile[] mediaFiles
    ) {
        int userId = extractUserIdFromToken(authorizationHeader);
        Employer employer = empployerService.getEmployerById(userId);
        if (employer == null) {
            return ResponseEntity.notFound().build();
        }
        if (mediaFiles != null && mediaFiles.length > 6) {
            throw new IllegalArgumentException("Maximum of 6 images allowed per job.");
        }


        Job job = new Job();
        job.setTitle(jobDto.getTitle());
        job.setDescription(jobDto.getDescription());
        job.setLocation(jobDto.getLocation());
        job.setSalary(jobDto.getSalary());
        job.setCategory(jobDto.getCategory());
        job.setEmployer(empployerService.getEmployerById(userId));
        job.setMissions(jobDto.getMissions());
        job.setCreatedAt(LocalDateTime.now());
        job.setStatus(JobStatus.OPEN);

        jobService.storeJobMediaImages(job, employer, mediaFiles);
        userService.addNotification(userId, "You created a new job successfully.");

        return ResponseEntity.ok(jobService.createJob(job));
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
        Employer employer = empployerService.getEmployerById(userId);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }
        if (review.rating() < 1 || review.rating() > 5) {
            return ResponseEntity.badRequest().body("Rating must be between 1 and 5");
        }
       try{
           Review newReview = new Review(
                   review.review(),
                   employer.getFirstname() + " " + employee.getLastname(),
                   review.rating()
           );
           newReview.setReviewedEmployee(employee);
           newReview.setReviewerId(userId);
           employee.addReview(employer, newReview.getContent(), newReview.getRating());
           reviewService.saveReview(newReview);
       }catch (InvalidPropertiesFormatException e){
              return ResponseEntity.badRequest().body("Invalid review format");
       }
        employeeService.saveEmployee(employee);

        userService.addNotification(userId, "You got a new review from " + employer.getFirstname() + " " + employer.getLastname());

        return ResponseEntity.ok("Review added successfully");
    }

    @GetMapping("/allApplicants")
    public ResponseEntity<List<Map<String, Object>>> getAllApplicants(
            @RequestHeader("Authorization") String authorizationHeader) throws IOException {

        int userId = extractUserIdFromToken(authorizationHeader);

        Employer employer = employerService.getEmployerById(userId);
        List<Job> jobs = employer.getJobOffers();

        // Map<Job, List<ApplicantDTO>> applicants to ApplicantDTO() and their states for each job
        Map<Job, List<ApplicantDTO>> applicantsMap = new HashMap<>();

        for (Job job : jobs) {
            List<ApplicantDTO> applicantsList = new ArrayList<>();
            Map<Integer, ApplicationState> jobApplicants = job.getApplicants();

            for (Map.Entry<Integer, ApplicationState> entry : jobApplicants.entrySet()) {
                int applicantId = entry.getKey();
                ApplicationState state = entry.getValue();
                Employee employee = employeeService.getEmployeeById(applicantId);
                ApplicantDTO applicantDTO = ObjectMapper.mapEmployeeApplicationToApplicantDTO(
                        employee,
                        RecommendationEngine.rankMatch(employee, job, jobService.getAllJobs()),
                        state
                );
                applicantsList.add(applicantDTO);
            }
            applicantsMap.put(job, applicantsList);
        }

        // Convert the map to a list of JSON-friendly objects
        List<Map<String, Object>> response = applicantsMap.entrySet().stream()
                .map(entry -> Map.of(
                        "job", entry.getKey(),
                        "applicants", entry.getValue()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    // update job
    @PostMapping("/job/{id}/update")
    public ResponseEntity<?> updateJob(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable int id,
            @RequestBody JobDTO jobDto){
        int userId = extractUserIdFromToken(authorizationHeader);

        Job job = jobService.getJobById(id);
        Employer emp = employerService.getEmployerById(userId);

        if(job.getEmployer().getId() != userId) {
            return ResponseEntity.ok()
                    .body(Map.of(
                            "status", "error",
                            "message", "Employee : " + emp.getFirstname() + " " + emp.getLastname() + ", You are not authorized to update this job"
                    )); // Forbidden
        }
        if (job == null) {
            return ResponseEntity.notFound().build();
        }
        job.setTitle(jobDto.getTitle());
        job.setCategory(jobDto.getCategory());
        job.setDescription(jobDto.getDescription());
        job.setLocation(jobDto.getLocation());
        job.setSalary(jobDto.getSalary());
        job.setMissions(jobDto.getMissions());
        job.setMedia(jobDto.getMedia());
        job.setStatus(jobDto.getStatus());

        jobService.updateJob(id, job);

        return ResponseEntity.ok()
                .body(Map.of(
                        "status", "success",
                        "message", "Successfully updated job with title" + job.getTitle() + " with id : " + id
                ));
    }

    // delete job
    @DeleteMapping("/job/{id}/delete")
    public ResponseEntity<?> deleteJob(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable int id){
        int userId = extractUserIdFromToken(authorizationHeader);

        Job job = jobService.getJobById(id);
        Employee emp = employeeService.getEmployeeById(userId);

        if(job.getEmployer().getId() != userId) {
            return ResponseEntity.ok()
                    .body(Map.of(
                            "status", "error",
                            "message", "Employee : " + emp.getFirstname() + " " + emp.getLastname() + ", You are not authorized to delete this job"
                    )); // Forbidden
        }


        jobService.deleteJobById(id);

        return ResponseEntity.ok()
                .body(Map.of(
                        "status", "success",
                        "message", "Successfully deleted job " + job.getTitle() + " with id : " + id
                ));
    }

    // update application status
    @PostMapping("/job/{id}/application/{employeeId}/updateStatus")
    public ResponseEntity<?> updateApplicationStatus(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable int id,
            @PathVariable int employeeId,
            @RequestBody ApplicationState status) {
        int userId = extractUserIdFromToken(authorizationHeader);

        Job job = jobService.getJobById(id);
        Employee emp = employeeService.getEmployeeById(userId);

        if(job.getEmployer().getId() != userId) {
            return ResponseEntity.ok()
                    .body(Map.of(
                            "status", "error",
                            "message", "Employee : " + emp.getFirstname() + " " + emp.getLastname() + ", You are not authorized to update this job"
                    )); // Forbidden
        }


        if(job.getApplicants().get(employeeId) == null) {
            return ResponseEntity.ok()
                    .body(Map.of(
                            "status", "error",
                            "message", "Employee with id : " + employeeId + " is not an applicant for this job"
                    )); // Forbidden
        }
        if(job.getApplicants().get(employeeId) == status ) {
            return ResponseEntity.ok()
                    .body(Map.of(
                            "status", "error",
                            "message", "Employee with id : " + employeeId + " already has this status"
                    )); // Forbidden
        }
        jobService.changeApplicationStatus(id, employeeId, status);

        return ResponseEntity.ok()
                .body(Map.of(
                        "status", "success",
                        "message", "Successfully updated application status for employee with id : " + employeeId + " to " + status
                ));
    }


}
