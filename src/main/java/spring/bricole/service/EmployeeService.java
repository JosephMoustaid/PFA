package spring.bricole.service;

import org.springframework.stereotype.Service;
import spring.bricole.common.*;
import spring.bricole.model.Conversation;
import spring.bricole.model.Employee;
import spring.bricole.model.Job;
import spring.bricole.model.Review;
import spring.bricole.repository.EmployeeRepository;
import spring.bricole.repository.ReviewRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final JobService jobService;
    private final EventLoggingService eventLoggingService;
    private final ReviewRepository reviewRepository;
    private final ConversationService conversationService;

    // Constructor injection
    public EmployeeService(EmployeeRepository employeeRepository,
                           JobService jobService,
                           EventLoggingService eventLoggingService,
                           ReviewRepository reviewRepository,
                           ConversationService conversationService) {
        this.employeeRepository = employeeRepository;
        this.jobService = jobService;
        this.eventLoggingService = eventLoggingService;
        this.reviewRepository = reviewRepository;
        this.conversationService = conversationService;
    }


    // get total number of employees
    public long getTotalEmployees() {
        return employeeRepository.count();
    }
    // Get all employees
    public List<Employee> getAllEmployees() {

        return employeeRepository.findAll(); // Uses JpaRepository's built-in method
    }

    // Get employee by ID
    public Employee getEmployeeById(Integer id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    // Register new employee
    public Employee registerEmployee(Employee employee) {
        // Add any business logic here (validation, password hashing, etc.)
        eventLoggingService.log(employee.getId(), Role.EMPLOYEE , EventType.SIGNUP, Map.of("email", employee.getEmail()));
        return employeeRepository.save(employee);
    }

    // Find by email
    public Optional<Employee> findByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    public boolean deleteEmployeeById(Integer id) {
        if (employeeRepository.existsById(id)) {
            Employee emp = employeeRepository.findById(id).orElse(null);
            // delete employee reviews
            for(Review r : emp.getReviews()) {
                reviewRepository.deleteById(r.getId());
            }
            // delete emoloyee conversations
            for (Conversation c : conversationService.getAllConversationsByUserId(emp.getId())){
                conversationService.deleteConversationById(c.getId());
            }
            employeeRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    // save updates
    public Employee saveEmployee(Employee employeeDetails) {
        return employeeRepository.save(employeeDetails);
    }
    public List<Employee> findByFullName(String fullName) {
        return employeeRepository.findByFullName(fullName);
    }

    public List<Employee> findBySkill(String skill) {
        return employeeRepository.findBySkill(skill);
    }
    public List<Employee> findByAddress(String address) {
        return employeeRepository.findByAddress(address);
    }

    // get applied jobs
    public Map<Job, ApplicationState>  getAppliedJobs(int employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        Map<Job, ApplicationState> appliedJobs = jobService.getApplicationsByEmployeeId(employee.getId());
        eventLoggingService.log(employeeId, Role.EMPLOYEE, EventType.FETCHED_APPLICATIONS, Map.of("action" , "fetched applications to jobs") );
        return appliedJobs;
    }

    // update avaialbility
    public Employee updateEmployeeAvailability(int employeeId, Map<String, Availability> availabilityDaysOfWeek) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        employee.setAvailabilityDaysOfWeek(availabilityDaysOfWeek);

        eventLoggingService.log(employeeId, Role.EMPLOYEE, EventType.UPDATE_AVAILABILITY, Map.of("action" , "updated availability") );
        return employeeRepository.save(employee);
    }

    // update job preferences
    public Employee updateJobPreferences(int employeeId, List<JobCategory> jobPreferences) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        employee.setJobPreferences(jobPreferences);
        eventLoggingService.log(employeeId, Role.EMPLOYEE, EventType.UPDATE_JOB_PREFERENCES, Map.of("action" , "updated job preferences"));
        return employeeRepository.save(employee);
    }

    // update skills
    public Employee updateSkills(int employeeId, List<Skill> skills) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        employee.setSkills(skills);
        eventLoggingService.log(employeeId, Role.EMPLOYEE, EventType.UPDATE_SKILLS, Map.of("action" , "updated skills"));
        return employeeRepository.save(employee);
    }
}