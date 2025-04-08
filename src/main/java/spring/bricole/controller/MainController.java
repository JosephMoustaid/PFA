package spring.bricole.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.bricole.dto.EmployeeConsultingResponse;
import spring.bricole.dto.ReviewResponse;
import spring.bricole.model.Employee;
import spring.bricole.model.Job;
import spring.bricole.model.Review;
import spring.bricole.service.EmployeeService;
import spring.bricole.service.JobService;
import spring.bricole.util.JwtUtil;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/main")
public class MainController {

    private final EmployeeService employeeService;
    private final JobService jobService;

    public MainController(EmployeeService employeeService, JobService jobService) {
        this.employeeService = employeeService;
        this.jobService = jobService;
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

    // DO NOT Requires authorization:

    // consult jobs
    @GetMapping("/jobs")
    public ResponseEntity<List<Job>> getAllJobs(){
        List<Job> jobs= jobService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }
    // get job by id
    @GetMapping("/jobs/search/{id}")
    public ResponseEntity<Job> getAllJobById(@PathVariable Integer id){
        Job job= jobService.getJobById(id);
        return ResponseEntity.ok(job);
    }
    // search jobs by title
    @GetMapping("/jobs/search/title/{title}")
    public ResponseEntity<List<Job>> getJobsByTitle(@PathVariable String title){
        List<Job> jobs= jobService.findByTitle(title);
        return ResponseEntity.ok(jobs);
    }
    @GetMapping("/jobs/search/description/{description}")
    public ResponseEntity<List<Job>> getJobsByDescription(@PathVariable String description){
        List<Job> jobs= jobService.findByDescription(description);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/jobs/search/status/{status}")
    public ResponseEntity<List<Job>> getJobsByStatus(@PathVariable String status){
        List<Job> jobs= jobService.findByJobStatus(status);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/jobs/search/trending")
    public ResponseEntity<List<Job>> getTrendingJobs(){
        List<Job> jobs= jobService.findTrendingJobs();
        return ResponseEntity.ok(jobs);
    }
    @GetMapping("/jobs/search/location/{location}")
    public ResponseEntity<List<Job>> getTrendingJobs(@PathVariable String location){
        List<Job> jobs= jobService.findByLocation(location);
        return ResponseEntity.ok(jobs);
    }
    @GetMapping("/jobs/search/highestpaying")
    public ResponseEntity<List<Job>> getHighestPayingJobs(){
        List<Job> jobs= jobService.findHighestPayingJobs();
        return ResponseEntity.ok(jobs);
    }
    // search jobs by category
    @GetMapping("/jobs/search/category/{category}")
    public ResponseEntity<List<Job>> getJobsByCategory(@PathVariable String category){
        List<Job> jobs= jobService.findByCategory(category);
        return ResponseEntity.ok(jobs);
    }
    // Requires authorization:
// Get all employees
    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeConsultingResponse>> getAllEmployees(@RequestHeader("Authorization") String auth) {
        int userId = extractUserIdFromToken(auth);

        List<EmployeeConsultingResponse> employees = employeeService.getAllEmployees()
                .stream()
                .map(employee -> new EmployeeConsultingResponse(
                        employee.getFirstname(),
                        employee.getLastname(),
                        employee.getPhoneNumberPrefix(),
                        employee.getPhoneNumber(),
                        employee.getSkills(),
                        employee.getProfilePicture(),
                        employee.getAvailabilityDaysOfWeek() ,
                        employee.getJobPreferences(),
                        employee.getReviews()
                ))
                .toList();
        return ResponseEntity.ok(employees);
    }

    // Get employee by ID
    @GetMapping("/employees/{id}")
    public ResponseEntity<EmployeeConsultingResponse> getEmployeeById(@PathVariable Integer id,
                                                                      @RequestHeader("Authorization") String auth) {
        int userId = extractUserIdFromToken(auth);

        Employee employee = employeeService.getEmployeeById(id);
        EmployeeConsultingResponse response = new EmployeeConsultingResponse(
                employee.getFirstname(),
                employee.getLastname(),
                employee.getPhoneNumberPrefix(),
                employee.getPhoneNumber(),
                employee.getSkills(),
                employee.getProfilePicture(),
                employee.getAvailabilityDaysOfWeek() ,
                employee.getJobPreferences(),
                employee.getReviews()
        );
        return ResponseEntity.ok(response);
    }

    // Get employee by email
    @GetMapping("/employees/email/{email}")
    public ResponseEntity<EmployeeConsultingResponse> getEmployeeByEmail(@PathVariable String email,
                                                                         @RequestHeader("Authorization") String auth) {
        int userId = extractUserIdFromToken(auth);

        Employee employee = employeeService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        EmployeeConsultingResponse response = new EmployeeConsultingResponse(
                employee.getFirstname(),
                employee.getLastname(),
                employee.getPhoneNumberPrefix(),
                employee.getPhoneNumber(),
                employee.getSkills(),
                employee.getProfilePicture(),
                employee.getAvailabilityDaysOfWeek() ,
                employee.getJobPreferences(),
                employee.getReviews()
        );
        return ResponseEntity.ok(response);
    }

    // Search employees by full name
    @GetMapping("/employees/search/fullname/{fullname}")
    public ResponseEntity<List<EmployeeConsultingResponse>> getEmployeesByFullName(@PathVariable String fullname,
                                                                                   @RequestHeader("Authorization") String auth) {
        int userId = extractUserIdFromToken(auth);

        List<EmployeeConsultingResponse> employees = employeeService.findByFullName(fullname)
                .stream()
                .map(employee -> new EmployeeConsultingResponse(
                        employee.getFirstname(),
                        employee.getLastname(),
                        employee.getPhoneNumberPrefix(),
                        employee.getPhoneNumber(),
                        employee.getSkills(),
                        employee.getProfilePicture(),
                        employee.getAvailabilityDaysOfWeek() ,
                        employee.getJobPreferences(),
                        employee.getReviews()
                ))
                .toList();
        return ResponseEntity.ok(employees);
    }

    // Search employees by skill
    @GetMapping("/employees/search/skill/{skill}")
    public ResponseEntity<List<EmployeeConsultingResponse>> getEmployeesBySkill(@PathVariable String skill,
                                                                                @RequestHeader("Authorization") String auth) {
        int userId = extractUserIdFromToken(auth);

        List<EmployeeConsultingResponse> employees = employeeService.findBySkill(skill)
                .stream()
                .map(employee -> new EmployeeConsultingResponse(
                        employee.getFirstname(),
                        employee.getLastname(),
                        employee.getPhoneNumberPrefix(),
                        employee.getPhoneNumber(),
                        employee.getSkills(),
                        employee.getProfilePicture(),
                        employee.getAvailabilityDaysOfWeek() ,
                        employee.getJobPreferences(),
                        employee.getReviews()
                ))
                .toList();
        return ResponseEntity.ok(employees);
    }

    // Search employees by location
    @GetMapping("/employees/search/location/{location}")
    public ResponseEntity<List<EmployeeConsultingResponse>> getEmployeesByLocation(@PathVariable String location,
                                                                                   @RequestHeader("Authorization") String auth) {
        int userId = extractUserIdFromToken(auth);

        List<EmployeeConsultingResponse> employees = employeeService.findByAddress(location)
                .stream()
                .map(employee -> new EmployeeConsultingResponse(
                        employee.getFirstname(),
                        employee.getLastname(),
                        employee.getPhoneNumberPrefix(),
                        employee.getPhoneNumber(),
                        employee.getSkills(),
                        employee.getProfilePicture(),
                        employee.getAvailabilityDaysOfWeek() ,
                        employee.getJobPreferences(),
                        employee.getReviews()
                ))
                .toList();
        return ResponseEntity.ok(employees);
    }

    // consulting employee reviews
    @GetMapping("/employees/reviews/{id}")
    public ResponseEntity<Set<ReviewResponse>> getEmployeeReviews(@PathVariable Integer id,
                                                                                @RequestHeader("Authorization") String auth) {
        int userId = extractUserIdFromToken(auth);

        Employee employee = employeeService.getEmployeeById(id);

        Set<Review> reviews = employee.getReviews();

        Set<ReviewResponse> reviewsResponse = reviews
                .stream()
                .map(review -> new ReviewResponse(
                        review.getId(),
                        review.getReviewerName(),
                        review.getContent(),
                        review.getRating(),
                        review.getCreatedAt(),
                        review.getReviewedEmployee().getId(),
                        review.getReviewerId()
                ))
                .collect(Collectors.toSet());
        return ResponseEntity.ok(reviewsResponse);
    }
}