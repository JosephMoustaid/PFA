package spring.bricole.service;

import org.springframework.stereotype.Service;
import spring.bricole.exceptions.UserNotFountException;
import spring.bricole.model.Admin;
import spring.bricole.model.Employer;
import spring.bricole.model.User;
import spring.bricole.repository.AdminRepository;
import spring.bricole.repository.EmployeeRepository;
import spring.bricole.exceptions.BadCredentialsException;

import spring.bricole.model.Employee;

import spring.bricole.repository.EmployerRepository;
import spring.bricole.repository.UserRepository;
import spring.bricole.service.Bcrypt;

import java.util.List;

@Service
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final EmployerRepository employerRepository;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    public AuthService(EmployeeRepository employeeRepository, EmployerRepository employerRepository,
                       AdminRepository adminRepository, UserRepository userRepository) {
        this.employeeRepository = employeeRepository;
        this.employerRepository = employerRepository;
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
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

    public int authentificateUser(String email, String password){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFountException("User not found"));
        if (Bcrypt.checkPassword(password, user.getPassword())) {
            return user.getId();
        } else {
            throw new BadCredentialsException("Invalid password");
        }
    }

    // get the user role from the user id
    public String getUserRole(int id) {
        List<Employee> employees = employeeRepository.findAll();
        List<Employer> employers = employerRepository.findAll();
        for(Employee employee : employees) {
            if (employee.getId() == id) {
                return "EMPLOYEE";
            }
        }
        return "EMPLOYER";
    }
    public String getUserFullname(int id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFountException("User not found"));
        return user.getFirstname() + " " + user.getLastname();
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