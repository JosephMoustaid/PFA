package spring.bricole.service;

import org.springframework.stereotype.Service;
import spring.bricole.exceptions.UserNotFountException;
import spring.bricole.repository.EmployeeRepository;
import spring.bricole.exceptions.BadCredentialsException;

import spring.bricole.model.Employee;


@Service
public class AuthService {

    private final EmployeeRepository employeeRepository;

    public AuthService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee authenticate(String email, String rawPassword) {

        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFountException("User not found"));

        if (Bcrypt.checkPassword(rawPassword, employee.getPassword())) {
            return employee;
        } else {
            throw new BadCredentialsException("Invalid password");
        }
    }
}