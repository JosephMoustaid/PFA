package spring.bricole.service;

import org.springframework.stereotype.Service;
import spring.bricole.model.Employee;
import spring.bricole.repository.EmployeeRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    // Constructor injection
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
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

}