package spring.bricole.service;

import org.springframework.stereotype.Service;
import spring.bricole.model.Employer;
import spring.bricole.repository.EmployerRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EmployerService {

    private final EmployerRepository employerRepository;

    public EmployerService(EmployerRepository employerRepository) {
        this.employerRepository = employerRepository;
    }

    // Get all employers
    public List<Employer> getAllEmployers() {
        return employerRepository.findAll();
    }

    // Get employer by ID
    public Employer getEmployerById(Integer id) {
        return employerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employer not found"));
    }

    // Register new employer
    public Employer registerEmployer(Employer employer) {
        // Add any business logic here (validation, etc.)
        return employerRepository.save(employer);
    }

    // Find by email
    public Optional<Employer> findByEmail(String email) {
        return employerRepository.findByEmail(email);
    }

    // Delete employer by ID
    public void deleteEmployerById(Integer id) {
        employerRepository.deleteById(id);
    }
}