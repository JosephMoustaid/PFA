package spring.bricole.service;

import org.springframework.stereotype.Service;
import spring.bricole.common.ApplicationState;
import spring.bricole.common.Availability;
import spring.bricole.common.JobCategory;
import spring.bricole.common.Skill;
import spring.bricole.model.Employee;
import spring.bricole.model.Job;
import spring.bricole.repository.EmployeeRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final JobService jobService;

    // Constructor injection
    public EmployeeService(EmployeeRepository employeeRepository,
                           JobService jobService) {
        this.employeeRepository = employeeRepository;
        this.jobService = jobService;
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
        return employeeRepository.save(employee);
    }

    // Find by email
    public Optional<Employee> findByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    public boolean deleteEmployeeById(Integer id) {
        if (employeeRepository.existsById(id)) {
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

        return appliedJobs;
    }

    // update avaialbility
    public Employee updateEmployeeAvailability(int employeeId, Map<String, Availability> availabilityDaysOfWeek) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        employee.setAvailabilityDaysOfWeek(availabilityDaysOfWeek);
        return employeeRepository.save(employee);
    }

    // update job preferences
    public Employee updateJobPreferences(int employeeId, List<JobCategory> jobPreferences) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        employee.setJobPreferences(jobPreferences);
        return employeeRepository.save(employee);
    }

    // update skills
    public Employee updateSkills(int employeeId, List<Skill> skills) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        employee.setSkills(skills);
        return employeeRepository.save(employee);
    }
}