package spring.bricole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.bricole.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
}
