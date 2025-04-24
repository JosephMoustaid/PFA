package spring.bricole.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.bricole.algorithms.RecommendationEngine;
import spring.bricole.common.ApplicationState;
import spring.bricole.common.Availability;
import spring.bricole.dto.*;
import spring.bricole.model.Employee;
import spring.bricole.model.Job;
import spring.bricole.model.Notification;
import spring.bricole.model.User;
import spring.bricole.service.EmployeeService;
import spring.bricole.service.JobService;
import spring.bricole.service.NotificationService;
import spring.bricole.service.UserService;
import spring.bricole.util.JwtUtil;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    private final JobService jobService;
    private final EmployeeService employeeService;
    private final UserService userService;

    public EmployeeController(EmployeeService employeeService, JobService jobService, NotificationService notificationService, UserService userService) {
        this.employeeService = employeeService;
        this.jobService = jobService;
        this.userService = userService;
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

            User user = userService.getUserById(userId);

            userService.addNotification(userId,"You applied to a new job succesfully" );

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

    @PostMapping("/profile/update/availabilty")
    public ResponseEntity<?> updateEmployeeAvailability(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody AvailabilityDTO availabilityDTO) {

        try{
            int userId = extractUserIdFromToken(authorizationHeader);
            employeeService.getEmployeeById(userId);

            Map<String , Availability> availabilityMap = new HashMap<>();
            availabilityMap.put("MONDAY" , availabilityDTO.mondayAvailability());
            availabilityMap.put("TUESDAY" , availabilityDTO.tuesdayAvailability());
            availabilityMap.put("WEDNESDAY" , availabilityDTO.wednesdayAvailability());
            availabilityMap.put("THURSDAY" , availabilityDTO.thursdayAvailability());
            availabilityMap.put("FRIDAY" , availabilityDTO.fridayAvailability());
            availabilityMap.put("SATURDAY" , availabilityDTO.saturdayAvailability());
            availabilityMap.put("SUNDAY" , availabilityDTO.sundayAvailability());

            employeeService.updateEmployeeAvailability(userId, availabilityMap);

            return ResponseEntity.ok()
                    .body(Map.of(
                            "status", "success",
                            "message", "Successfully updated employee availability"
                    ));
        }catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "status", "error",
                            "message", "Failed to apply for job: " + e.getMessage()
                    ));
        }
    }

    @PostMapping("/profile/update/jobPreferences")
    public ResponseEntity<?> updateEmployeeJobPreferences(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody JobPrefrencesDTO jobPrefrencesDTO) {

        try{
            int userId = extractUserIdFromToken(authorizationHeader);
            employeeService.getEmployeeById(userId);


            employeeService.updateJobPreferences(userId, jobPrefrencesDTO.jobPreferences());

            return ResponseEntity.ok()
                    .body(Map.of(
                            "status", "success",
                            "message", "Successfully updated employee job preferences"
                    ));
        }catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "status", "error",
                            "message", "Failed to apply for job: " + e.getMessage()
                    ));
        }
    }


    @PostMapping("/profile/update/skills")
    public ResponseEntity<?> updateEmployeeSkills(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody SkillsDTO skillsDto) {

        try{
            int userId = extractUserIdFromToken(authorizationHeader);
            employeeService.getEmployeeById(userId);


            employeeService.updateSkills(userId, skillsDto.skills());

            return ResponseEntity.ok()
                    .body(Map.of(
                            "status", "success",
                            "message", "Successfully updated employee skills"
                    ));
        }catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "status", "error",
                            "message", "Failed to apply for job: " + e.getMessage()
                    ));
        }
    }
    @DeleteMapping("/application/{id}/cancel")
    public ResponseEntity<?> cancelApplication(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable int id) {

        try {
            int userId = extractUserIdFromToken(authorizationHeader);
            employeeService.getEmployeeById(userId);

            Map<Job, ApplicationState> applications = jobService.getApplicationsByEmployeeId(userId);
            if (applications == null || applications.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "status", "error",
                                "message", "No applications found for the user"
                        ));
            }

            for (Map.Entry<Job, ApplicationState> entry : applications.entrySet()) {
                Job job = entry.getKey();
                if (job.getId() == id) {
                    if (entry.getValue() == ApplicationState.CANCELED) {
                        return ResponseEntity.badRequest()
                                .body(Map.of(
                                        "status", "error",
                                        "message", "Application is already canceled"
                                ));
                    }

                    jobService.changeApplicationStatus(id, userId, ApplicationState.CANCELED);
                    return ResponseEntity.ok()
                            .body(Map.of(
                                    "status", "success",
                                    "message", "Successfully canceled application"
                            ));
                }
            }

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "status", "error",
                            "message", "Application not found"
                    ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "status", "error",
                            "message", "Failed to cancel application: " + e.getMessage()
                    ));
        }
    }
}