package spring.bricole.service;

import org.springframework.stereotype.Service;
import spring.bricole.exceptions.UserNotFountException;
import spring.bricole.model.Admin;
import spring.bricole.model.Employer;
import spring.bricole.repository.AdminRepository;
import spring.bricole.repository.EmployeeRepository;
import spring.bricole.exceptions.BadCredentialsException;

import spring.bricole.model.Employee;

import spring.bricole.repository.EmployerRepository;
import spring.bricole.service.Bcrypt;

@Service
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final EmployerRepository employerRepository;
    private final AdminRepository adminRepository;

    public AuthService(EmployeeRepository employeeRepository, EmployerRepository employerRepository, AdminRepository adminRepository) {
        this.employeeRepository = employeeRepository;
        this.employerRepository = employerRepository;
        this.adminRepository = adminRepository;
    }

    public Employer authenticateEmployer(String email, String rawPassword) {

        Employer employer = employerRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFountException("User not found"));

        if (Bcrypt.checkPassword(rawPassword, employer.getPassword())) {
            return employer;
        } else {
            throw new BadCredentialsException("Invalid password");
        }
    }
    public Employee authenticateEmployee(String email, String rawPassword) {

        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFountException("User not found"));

        if (Bcrypt.checkPassword(rawPassword, employee.getPassword())) {
            return employee;
        } else {
            throw new BadCredentialsException("Invalid password");
        }
    }
    public Admin authenticateAdmin(String email, String rawPassword) {

        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFountException("User not found"));

        if (Bcrypt.checkPassword(rawPassword, admin.getPassword())) {
            return admin;
        } else {
            throw new BadCredentialsException("Invalid password");
        }
    }
}