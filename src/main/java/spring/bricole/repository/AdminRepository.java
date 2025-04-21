package spring.bricole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.bricole.model.Admin;
import spring.bricole.model.Employer;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    // Data retrieval methods:

    // Find admin by email, because the password is hashed , the service will handle the password verification
    Optional<Admin> findByEmail(String email);
}
