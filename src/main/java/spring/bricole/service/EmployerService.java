package spring.bricole.service;

import org.springframework.stereotype.Service;
import spring.bricole.common.EventType;
import spring.bricole.common.Role;
import spring.bricole.model.Employer;
import spring.bricole.model.Job;
import spring.bricole.repository.EmployerRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EmployerService {

    private final EmployerRepository employerRepository;
    private final EventLoggingService eventLoggingService;
    // get total number of employers
    public long getTotalEmployers() {
        return employerRepository.count();
    }

    public EmployerService(EmployerRepository employerRepository,
                           EventLoggingService eventLoggingService) {
        this.employerRepository = employerRepository;
        this.eventLoggingService = eventLoggingService;
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
        eventLoggingService.log(employer.getId() , Role.EMPLOYER , EventType.SIGNUP, Map.of("email", employer.getEmail()));
        return employerRepository.save(employer);
    }

    // Find by email
    public Optional<Employer> findByEmail(String email) {
        return employerRepository.findByEmail(email);
    }

    // Delete employer by ID
    public boolean deleteEmployerById(Integer id) {
        employerRepository.deleteById(id);
        return false;
    }

    // delete and employer
    public Boolean deleteEmployerById(int id) {
        if(employerRepository.existsById(id)){
            employerRepository.deleteById(id);
            return true;
        }
        return false;
    }


}
